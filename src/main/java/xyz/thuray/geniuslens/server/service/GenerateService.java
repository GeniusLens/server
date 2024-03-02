package xyz.thuray.geniuslens.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import xyz.thuray.geniuslens.server.data.context.InferenceCtx;
import xyz.thuray.geniuslens.server.data.dto.*;
import xyz.thuray.geniuslens.server.data.enums.InferenceStatus;
import xyz.thuray.geniuslens.server.data.enums.MessageSender;
import xyz.thuray.geniuslens.server.data.enums.MessageStatus;
import xyz.thuray.geniuslens.server.data.enums.MessageType;
import xyz.thuray.geniuslens.server.data.po.*;
import xyz.thuray.geniuslens.server.data.vo.Result;
import xyz.thuray.geniuslens.server.mapper.*;
import xyz.thuray.geniuslens.server.util.SHAUtil;
import xyz.thuray.geniuslens.server.util.UserContext;
import xyz.thuray.geniuslens.server.util.UUIDUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.apache.commons.codec.digest.DigestUtils.*;

@Service
@Slf4j
public class GenerateService {
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private FunctionMapper functionMapper;
    @Resource
    private LoraMapper loraMapper;
    @Resource
    private SampleMapper sampleMapper;
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private TaskMapper taskMapper;
    @Resource
    private InferenceService inferenceService;
    @Resource
    private KafkaTemplate<String, InferenceCtx> kafkaTemplate;


    public Result<?> createCategory(CategoryParamDTO param) {
        CategoryPO po = categoryMapper.selectByName(param.getName());
        if (po != null) {
            return Result.fail("分类已存在");
        }
        CategoryPO parent = categoryMapper.selectByName(param.getParent());
        po = CategoryParamDTO.toPO(param, (parent == null) ? 0 : parent.getId());
        try {
            categoryMapper.insert(po);
        } catch (Exception e) {
            log.error("createCategory error", e);
            return Result.fail("创建失败: " + e.getMessage());
        }

        return Result.success(po);
    }

    public Result<?> getCategoryList(String name) {
        List<CategoryPO> list;
        if (name == null) {
            list = categoryMapper.selectAllParent();
            // 添加一个空的"全部"
            CategoryPO all = new CategoryPO();
            all.setName("全部");
            all.setId(0L);
            list.add(0, all);
        } else {
            if ("全部".equals(name)) {
                list = categoryMapper.selectAll();
            } else {
                list = categoryMapper.selectByParentName(name);
            }
        }
        return Result.success(list);
    }

    public Result<?> getFunctionList(String category) {
        List<FunctionPO> list = functionMapper.selectByCategoryName(category);
        return Result.success(list);
    }

    public Result<?> getRecommendFunctionList(String type) {
        List<FunctionPO> list = functionMapper.selectRecommend();
        return Result.success(list);
    }

    public Result<?> createFunction(FunctionParamDTO dto) {
        CategoryPO category = categoryMapper.selectByName(dto.getCategory());
        if (category == null) {
            return Result.fail("分类不存在");
        }
        FunctionPO po = FunctionParamDTO.toPO(dto, category.getId());
        try {
            functionMapper.insert(po);
        } catch (Exception e) {
            log.error("createFunction error", e);
            return Result.fail("创建失败: " + e.getMessage());
        }

        return Result.success(po);
    }

    public Result<?> getFunction(Long id) {
        FunctionPO po = functionMapper.selectById(id);
        if (po == null) {
            return Result.fail("功能不存在");
        }
        return Result.success(po);
    }

    public Result<?> createSample(List<SampleParamDTO> sample) {
        List<SamplePO> list = new ArrayList<>();
        for (SampleParamDTO dto : sample) {
            list.add(SampleParamDTO.toPO(dto));
        }
        log.info("createSample: {}", list);
        try {
            sampleMapper.insertBatch(list);
        } catch (Exception e) {
            log.error("createSample error", e);
            return Result.fail("创建失败: " + e.getMessage());
        }

        return Result.success(list);
    }

    public Result<?> getSampleList(Long functionId, Integer type) {
        List<SamplePO> list = sampleMapper.selectAllByFunctionId(functionId, type);
        return Result.success(list);
    }

    public Result<?> createLora(LoraParamDTO dto) {
        Long userId = UserContext.getUserId();
        LoraPO po = LoraParamDTO.toPO(dto, userId);
        try {
            loraMapper.insert(po);
        } catch (Exception e) {
            log.error("createLora error", e);
            return Result.fail("创建失败: " + e.getMessage());
        }

        return Result.success(po);
    }

    public Result<?> getLoraList() {
        Long userId = UserContext.getUserId();
        List<LoraPO> list = loraMapper.selectByUserId(userId);
        return Result.success(list);
    }

    public Result<?> deleteLora(Long id) {
        Long userId = UserContext.getUserId();
        LoraPO po = loraMapper.selectById(id);
        if (po == null) {
            return Result.fail("Lora不存在");
        }
        if (!po.getUserId().equals(userId)) {
            return Result.fail("无权删除");
        }
        po.setDeleted(true);
        loraMapper.updateById(po);

        return Result.success();
    }

    public Result<?> createInference(TaskParamDTO dto) {
        log.info("Start inference: {}", dto);
        FunctionPO function = functionMapper.selectByName(dto.getFunction());
//        if (function == null) {
//            return Result.fail("功能不存在");
//        }
        List<LoraPO> loraList = loraMapper.selectById(dto.getLoraIds());
//        if (loraList.size() != dto.getLoraIds().size()) {
//            return Result.fail("Lora不存在");
//        }

        // 初始化Task
        InferenceCtx ctx = initTask(function, loraList, dto);

        // 发送kafka消息
        CompletableFuture<SendResult<String, InferenceCtx>> future = kafkaTemplate.send("inference", ctx.getTask().getTaskId(), ctx);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Inference {} send kafka error: {}", ctx.getTask().getTaskId(), ex.getMessage());
            } else {
                log.info("Inference {} send kafka success: {}", ctx.getTask().getTaskId(), result.getRecordMetadata());
            }
        });

        return Result.success(ctx.getTask());
    }

    @KafkaListener(topics = {"inference"}, groupId = "group")
    public void consumeMessage(ConsumerRecord<String, String> record) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        log.info("消费者消费topic:{} partition:{}的消息 -> {}", record.topic(), record.partition(), record.value());

        // 解析消息
        InferenceCtx ctx;
        try {
            ctx = objectMapper.readValue(record.value(), InferenceCtx.class);
        } catch (JsonProcessingException e) {
            log.error("Inference consumeMessage error", e);
            return;
        }

        // 更新任务状态
        updateTaskStatus(ctx, InferenceStatus.PROCESSING);

        // 推理
        Response<Result> response;
        LocalDateTime start = LocalDateTime.now();
        try {
            response = inferenceService.singleLoraInfer(SingleLoraDTO.demo());
        } catch (Exception e) {
            log.error("推理失败:{}", e.getMessage());
            // 更新任务状态
            updateTaskStatus(ctx, InferenceStatus.FAILED);
            return;
        }
        LocalDateTime end = LocalDateTime.now();
        log.debug("response: {}", response);
        log.debug("推理耗时:{}", end.minusSeconds(start.getSecond()));
        log.debug("推理结果:{}", response.body());

        // 更新任务状态
        updateTaskStatus(ctx, InferenceStatus.FINISHED);
        log.info("Inference finished: {}", ctx.getTask().getTaskId());
    }

    private InferenceCtx initTask(FunctionPO function, List<LoraPO> loraList, TaskParamDTO dto) {
        // 生成taskId
        String taskId = UUIDUtil.generate();
        taskId = SHAUtil.MD5(taskId);
        log.info("Initializing task: {}", taskId);

        // 生成message
        MessagePO message = MessagePO.builder()
                .message(taskId + "已创建，请等待处理")
                .senderId(MessageSender.SYSTEM.getValue())
                .receiverId(UserContext.getUserId())
                .type(MessageType.SYSTEM.getValue())
                .build();
        try {
            messageMapper.insert(message);
        } catch (Exception e) {
            log.error("initTask error", e);
        }
        log.debug("initTask message: {}", message);

        // 生成Task
        TaskPO task = TaskPO.builder()
                .taskId(taskId)
                .status(InferenceStatus.PENDING.getValue())
                // TODO: 回头得加上functionId
                // .functionId(function.getId())
                .userId(UserContext.getUserId())
                .messageId(message.getId())
                .build();
        try {
            // 保存Task
            taskMapper.insert(task);
        } catch (Exception e) {
            log.error("initTask error", e);
        }
        log.debug("initTask task: {}", task);

        InferenceCtx ctx = InferenceCtx.builder()
                .task(task)
                .status(InferenceStatus.PENDING)
                .function(function)
                .message(message)
                .loras(loraList)
                .sourceImages(dto.getSourceImages())
                .build();

        log.info("initTask: {}", ctx);
        return ctx;
    }

    private void updateTaskStatus(InferenceCtx ctx, InferenceStatus status) {
        // 更新ctx状态
        ctx.setStatus(status);

        // 处理Task状态
        TaskPO task = ctx.getTask();
        task.setStatus(status.getValue());
        taskMapper.updateById(task);
        // if (InferenceStatus.FINISHED.equals(status) || InferenceStatus.FAILED.equals(status)) {
        //     task.setDeleted(true);
        //     taskMapper.updateById(task);
        // }

        // 更新Message
        MessagePO message = ctx.getMessage();
        message.setStatus(status.getValue());
        switch (status) {
            case PENDING:
                message.setMessage(task.getTaskId() + "等待推理");
                break;
            case PROCESSING:
                message.setMessage(task.getTaskId() + "开始推理");
                break;
            case FINISHED:
                message.setMessage(task.getTaskId() + "推理完成");
                break;
            case FAILED:
                message.setMessage(task.getTaskId() + "推理失败");
                break;
        }
        // 将消息状态改为已读
        if (Objects.equals(MessageStatus.UNREAD.getValue(), message.getStatus())) {
            message.setStatus(MessageStatus.READ.getValue());
        }
        messageMapper.updateById(message);
    }
}
