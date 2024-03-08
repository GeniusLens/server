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
import xyz.thuray.geniuslens.server.data.dto.sd.*;
import xyz.thuray.geniuslens.server.data.enums.InferenceStatus;
import xyz.thuray.geniuslens.server.data.enums.MessageSender;
import xyz.thuray.geniuslens.server.data.enums.MessageStatus;
import xyz.thuray.geniuslens.server.data.enums.MessageType;
import xyz.thuray.geniuslens.server.data.po.*;
import xyz.thuray.geniuslens.server.data.vo.Result;
import xyz.thuray.geniuslens.server.data.vo.TaskVO;
import xyz.thuray.geniuslens.server.mapper.*;
import xyz.thuray.geniuslens.server.util.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static xyz.thuray.geniuslens.server.data.enums.InferenceStatus.PENDING;
import static xyz.thuray.geniuslens.server.data.enums.InferenceStatus.PROCESSING;

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
    @Resource
    private LockUtil lockUtil;

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

        FunctionPO function = null;
        List<LoraPO> loraList = null;
        if (dto.getTaskType() == 1) {
            log.info("TaskType: {}", dto.getTaskType());
            function = functionMapper.selectById(Long.parseLong(dto.getFunction()));
            if (function == null) {
                return Result.fail("功能不存在");
            }
            loraList = loraMapper.selectById(dto.getLoraIds());
//        if (loraList.size() != dto.getLoraIds().size()) {
//            return Result.fail("Lora不存在");
//        }
        } else if (dto.getTaskType() == 2) {
            log.info("TaskType: {}", dto.getTaskType());
        } else {
            log.error("TaskType error");
            return Result.fail("TaskType error");
        }

        // 初始化Task
        InferenceCtx ctx = initTask(function, loraList, dto);

        // 发送kafka消息
        String key = ctx.getTask() == null ? ctx.getLora().getName() : ctx.getTask().getTaskId();
        CompletableFuture<SendResult<String, InferenceCtx>> future = kafkaTemplate.send("inference", key, ctx);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Inference {} send kafka error: {}", ctx.getTask().getTaskId(), ex.getMessage());
            } else {
                log.info("Inference {} send kafka success: {}", ctx.getTask().getTaskId(), result.getRecordMetadata());
            }
        });

        return Result.success(ctx.getTask());
    }

    public Result<?> getUserInferenceList() {
        Long userId = UserContext.getUserId();
        log.info("getUserInferenceList: {}", userId);
        List<TaskVO> list = taskMapper.selectAllByUserId(userId);
        list.forEach(task -> {
            String status = switch (task.getStatus()) {
                case 1 -> "等待推理";
                case 2 -> "推理中";
                case 3 -> "推理完成";
                case 4 -> "推理失败";
                default -> "";
            };
            task.setStatusStr(status);
            task.setCreatedAtStr(TimeFormatUtil.format(task.getCreatedAt()));
        });
        log.info("getUserInferenceList: {}", list);
        return Result.success(list);
    }

    public Result<?> getInference(Long id) {
        TaskVO task = taskMapper.selectAllById(id);

        String status = switch (task.getStatus()) {
            case 1 -> "等待推理";
            case 2 -> "推理中";
            case 3 -> "推理完成";
            case 4 -> "推理失败";
            default -> "";
        };
        task.setStatusStr(status);
        task.setCreatedAtStr(TimeFormatUtil.format(task.getCreatedAt()));

        log.info("getInference: {}", task);
        return Result.success(task);
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
        updateTaskStatus(ctx, PROCESSING);

        // 等待获取锁
        // log.debug("等待获取锁");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!lockUtil.lock("inference", "inference")) {
                    log.debug("等待获取锁");
                } else {
                    log.debug("获取到锁");
                    // 停止等待
                    timer.cancel();

                    // 推理
                    Response<Result<Void>> response;
                    try {
                        response = inferRequest(ctx);
                    } catch (Exception e) {
                        log.error("推理失败:{}", e.getMessage());
                        // 释放锁
                        if (!lockUtil.unlock("inference", "inference")) {
                            log.error("释放锁失败");
                        }
                        // 更新任务状态
                        updateTaskStatus(ctx, InferenceStatus.FAILED);
                        return;
                    }

                    log.debug("submit: {}", response + " " + response.body());
                    Timer statusTimer = new Timer();
                    AtomicReference<Response<TaskStatusDTO>> statusResponse = new AtomicReference<>();
                    statusTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                String id = ctx.getTask() == null ? ctx.getLora().getName() : ctx.getTask().getTaskId();
                                statusResponse.set(inferenceService.getTaskStatus(new GetTaskDTO(id)));
                            } catch (Exception e) {
                                log.error("推理失败:{}", e.getMessage());
                                // 释放锁
                                if (!lockUtil.unlock("inference", "inference")) {
                                    log.error("释放锁失败");
                                }
                                // 更新任务状态
                                updateTaskStatus(ctx, InferenceStatus.FAILED);
                            }

                            // 判断推理是否完成
                            if (statusResponse.get() != null
                                    && statusResponse.get().body() != null
                            ) {
                                if (Objects.equals(Objects.requireNonNull(statusResponse.get().body()).getStatus(), "完成")) {
                                    log.debug("推理完成:{}", statusResponse.get().body());
                                    statusTimer.cancel();

                                    // 释放锁
                                    if (!lockUtil.unlock("inference", "inference")) {
                                        log.error("释放锁失败");
                                    }

                                    // 更新任务状态
                                    updateTaskStatus(ctx, InferenceStatus.FINISHED);
                                    // 添加result
                                    log.debug("status Result: {}", statusResponse.get());
                                    if (statusResponse.get() != null && statusResponse.get().body() != null) {
                                        if (ctx.getTaskType() == 1 && !ctx.getFunction().getType().equals("tryon")) {
                                            ctx.getTask().setResult(Objects.requireNonNull(statusResponse.get().body()).getResult());
                                            taskMapper.updateById(ctx.getTask());
                                        } else if (ctx.getTaskType() == 1 && ctx.getFunction().getType().equals("tryon")) {
                                            String result = Objects.requireNonNull(statusResponse.get().body()).getResult();
                                        }
                                    } else {
                                        log.error("推理失败:{}", statusResponse.get().errorBody());
                                    }
                                } else if (Objects.equals(Objects.requireNonNull(statusResponse.get().body()).getStatus(), "任务运行失败")) {
                                    log.debug("推理失败:{}", statusResponse.get().body());
                                    statusTimer.cancel();

                                    // 释放锁
                                    if (!lockUtil.unlock("inference", "inference")) {
                                        log.error("释放锁失败");
                                    }

                                    // 更新任务状态
                                    updateTaskStatus(ctx, InferenceStatus.FAILED);
                                }
                            }
                        }
                    }, 0, 1000);
                }
            }
        }, 0, 1000);

//        log.info("Inference finished: {}", ctx.getTask().getTaskId());
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
        TaskPO task = null;
        LoraPO lora = null;
        if (dto.getTaskType() == 1) {
            task = TaskPO.builder()
                    .taskId(taskId)
                    .status(PENDING.getValue())
                    // TODO: 回头得加上functionId
                    .functionId(2L)
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

        } else if (dto.getTaskType() == 2) {
            lora = LoraPO.builder()
                    .userId(UserContext.getUserId())
                    .name(taskId)
                    .images(dto.getSourceImages().toString())
                    .avatar(dto.getSourceImages().get(0))
                    .status(1)
                    .build();
            try {
                // 保存Task
                loraMapper.insert(lora);
            } catch (Exception e) {
                log.error("initTask error", e);
            }
        }
        InferenceCtx ctx = InferenceCtx.builder()
                .taskType(dto.getTaskType())
                .task(task)
                .lora(lora)
                .status(PENDING)
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
        if (task != null) {
            task.setStatus(status.getValue());
            taskMapper.updateById(task);
        }
        // if (InferenceStatus.FINISHED.equals(status) || InferenceStatus.FAILED.equals(status)) {
        //     task.setDeleted(true);
        //     taskMapper.updateById(task);
        // }

        // 处理Lora
        LoraPO lora = ctx.getLora();
        if (lora != null) {
            lora.setStatus(status.getValue());
            loraMapper.updateById(lora);
        }

        // 更新Message
        MessagePO message = ctx.getMessage();
        message.setStatus(status.getValue());
        if (task != null && task.getTaskId() != null) {
            updateTaskMessage(status, task, message);
        } else if (lora != null) {
            updateLoraMessage(status, lora, message);
        }
        // 将消息状态改为已读
        if (Objects.equals(MessageStatus.UNREAD.getValue(), message.getStatus())) {
            message.setStatus(MessageStatus.READ.getValue());
        }
        messageMapper.updateById(message);
    }

    private Response<Result<Void>> inferRequest(InferenceCtx ctx) {
        if (ctx.getTaskType() == 1) {
            FunctionPO function = ctx.getFunction();
            log.info("inferRequest: {}", function);
            if (Objects.equals(function.getType(), "solo")) {
                return inferenceService.singleLoraInfer(SingleLoraDTO.fromCtx(ctx));
            } else if (Objects.equals(function.getType(), "multi")) {
                return inferenceService.multiLoraInfer(MultiLoraDTO.fromCtx(ctx));
            } else if (Objects.equals(function.getType(), "scene")) {
                return inferenceService.sceneInfer(SceneDTO.fromCtx(ctx, "chinese_makeup"));
            } else if (Objects.equals(function.getType(), "video_scene")) {
                return inferenceService.videoInfer(VideoDTO.fromCtxToScene(ctx, "chinese_makeup"));
            } else if (Objects.equals(function.getType(), "video_real")) {
                return inferenceService.videoInfer(VideoDTO.fromCtxToReal(ctx));
            } else if (Objects.equals(function.getType(), "video_video")) {
                return inferenceService.videoInfer(VideoDTO.fromCtxToVideo(ctx));
            } else if (Objects.equals(function.getType(), "tryon")) {
                return inferenceService.tryOn(TryOnDTO.fromCtx(ctx));
            }
        } else if (ctx.getTaskType() == 2) {
            log.info("inferRequest: {}", ctx);
            return inferenceService.loraPersonTrain(LoraTrainingDTO.fromCtx(ctx));
        }

        return null;
    }

    private void updateTaskMessage(InferenceStatus status, TaskPO task, MessagePO message) {
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
    }

    private void updateLoraMessage(InferenceStatus status, LoraPO lora, MessagePO message) {
        switch (status) {
            case PENDING:
                message.setMessage("分身等待训练");
                break;
            case PROCESSING:
                message.setMessage("分身开始训练");
                break;
            case FINISHED:
                message.setMessage("分身训练完成");
                break;
            case FAILED:
                message.setMessage("分身训练失败");
                break;
        }
    }
}
