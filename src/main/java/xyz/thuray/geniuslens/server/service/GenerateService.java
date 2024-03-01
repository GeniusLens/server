package xyz.thuray.geniuslens.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import retrofit2.Response;
import xyz.thuray.geniuslens.server.data.context.InferenceCtx;
import xyz.thuray.geniuslens.server.data.dto.*;
import xyz.thuray.geniuslens.server.data.enums.InferenceStatus;
import xyz.thuray.geniuslens.server.data.po.*;
import xyz.thuray.geniuslens.server.data.vo.Result;
import xyz.thuray.geniuslens.server.mapper.*;
import xyz.thuray.geniuslens.server.util.UserContext;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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

    public Result<?> createInference(InferenceParamDTO dto) {
        log.info("createInference: {}", dto);
        FunctionPO function = functionMapper.selectByName(dto.getFunction());
//        if (function == null) {
//            return Result.fail("功能不存在");
//        }
        List<LoraPO> loraList = loraMapper.selectById(dto.getLoraIds());
//        if (loraList.size() != dto.getLoraIds().size()) {
//            return Result.fail("Lora不存在");
//        }
        String taskId = UUID.randomUUID().toString();
        MessagePO message = InferenceParamDTO.toMessage(dto, UserContext.getUserId());
        log.info("createInference: {}", message);
        try {
            messageMapper.insert(message);
        } catch (Exception e) {
            log.error("createInference error", e);
            return Result.fail("创建失败: " + e.getMessage());
        }
        InferenceCtx ctx = InferenceCtx.builder()
                .taskId(taskId)
                .status(InferenceStatus.PENDING)
                .function(function)
                .loras(loraList)
                .sourceImages(dto.getSourceImages())
                .message(message)
                .build();
        log.info("createInference: {}", ctx);

        CompletableFuture<SendResult<String, InferenceCtx>> future = kafkaTemplate.send("inference", ctx);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("send kafka error", ex);
            } else {
                log.info("send kafka success");
            }
        });

        return Result.success();
    }

    @KafkaListener(topics = {"inference"}, groupId = "group")
    public void consumeMessage(ConsumerRecord<String, String> record) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            InferenceCtx ctx = objectMapper.readValue(record.value(), InferenceCtx.class);
            log.info("消费者消费topic:{} partition:{}的消息 -> {}", record.topic(), record.partition(), ctx.toString());
            MessagePO message = ctx.getMessage();
            message.setMessage(ctx.getTaskId() + "开始推理");
            message.setStatus(1);
            messageMapper.updateById(message);


            Response<Result> response;
            LocalDateTime start = LocalDateTime.now();
            try {
                response = inferenceService.singleLoraInfer(SingleLora.demo());
            } catch (Exception e) {
                log.error("推理失败:{}", e.getMessage());
                return;
            }
            LocalDateTime end = LocalDateTime.now();
            log.info("response: {}", response);
            log.info("推理耗时:{}", end.minusSeconds(start.getSecond()));
            log.info("推理结果:{}", response.body());
        } catch (JsonProcessingException e) {
            log.error("消息消费失败:{}", e.getMessage());
        }
    }
}
