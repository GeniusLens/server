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
import java.util.concurrent.atomic.AtomicInteger;
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
    private ClothMapper clothMapper;
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
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        List<LoraPO> loraList = loraMapper.selectById(ids);
        if (loraList.size() != 1) {
            return Result.fail("Lora不存在");
        }
        LoraPO lora = loraList.get(0);
        if (lora == null) {
            return Result.fail("Lora不存在");
        }
        if (!lora.getUserId().equals(userId)) {
            return Result.fail("无权删除");
        }
        loraMapper.deleteById(id);

        return Result.success();
    }

    public Result<?> updateLoraName(Map<String, Object> map) {
        Long loraId = Long.parseLong(map.get("id").toString());
        String name = map.get("name").toString();

        List<Long> ids = new ArrayList<>();
        ids.add(loraId);
        List<LoraPO> loraList = loraMapper.selectById(ids);
        if (loraList.size() != 1) {
            return Result.fail("Lora不存在");
        }
        LoraPO lora = loraList.get(0);

        lora.setDescription(name);
        loraMapper.updateById(lora);

        return Result.success(lora);
    }

    public Result<?> createInference(TaskParamDTO dto) {
        log.info("Start create inference: {}", dto);

        FunctionPO function = null;
        List<LoraPO> loraList = null;
        if (dto.getTaskType() == 1) {
            // 获取本次推理的功能信息
            function = functionMapper.selectById(Long.parseLong(dto.getFunction()));
            if (function == null) {
                return Result.fail("功能不存在");
            }
            log.debug("Function: {}", function);
            // 除了这些功能，其他功能都需要lora
            if (!(Objects.equals(function.getType(), "tryon") || Objects.equals(function.getType(), "anime"))) {
                loraList = loraMapper.selectById(dto.getLoraIds());
                if (loraList.size() != dto.getLoraIds().size()) {
                    return Result.fail("Lora不存在");
                }
                log.debug("Lora: {}", loraList);
            }
            // 处理训练任务
        } else if (dto.getTaskType() == 2) {
            log.debug("训练任务，不需要function和lora");
        } else {
            log.error("TaskType error");
            return Result.fail("任务类型错误");
        }

        // 初始化Task
        InferenceCtx ctx = initTask(function, loraList, dto);

        // 发送kafka消息
        String key = ctx.getTask() == null ? ctx.getLoraToTrain().getName() : ctx.getTask().getTaskId();
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
        log.info("getInference: {}", id);
        TaskVO task = taskMapper.selectAllById(id);
        if (task == null) {
            return Result.fail("任务不存在");
        }

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
            log.error("序列化解析失败:{}", e.getMessage());
            return;
        }
        log.debug("消息解析成功:{}", ctx);

        // 获取分布式锁
        Timer timer = new Timer();
        AtomicInteger count = new AtomicInteger(0);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!lockUtil.lock("inference", "inference")) {
                    count.getAndIncrement();
                } else {
                    // 停止等待
                    timer.cancel();
                    log.info("获取锁成功 共尝试{}次", count);
                    // 更新任务状态
                    updateTaskStatus(ctx, PROCESSING);
                    // 准备进行推理
                    Response<Result<Void>> response;
                    try {
                        response = inferRequest(ctx);
                    } catch (Exception e) {
                        log.error("推理失败:{}", e.getMessage());
                        if (!lockUtil.unlock("inference", "inference")) {
                            log.error("释放锁失败");
                        }
                        // 更新任务状态
                        updateTaskStatus(ctx, InferenceStatus.FAILED);
                        return;
                    }
                    log.debug("任务提交成功：{}", response);

                    // 使用一个新的Timer来轮询任务状态
                    Timer statusTimer = new Timer();
                    // 为了保证多线程安全，使用AtomicReference
                    AtomicReference<Response<TaskStatusDTO>> statusResponse = new AtomicReference<>();
                    statusTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                // 计算需要使用的id
                                // 如果是训练任务，使用lora的name
                                // 如果是推理任务，使用task的taskId
                                String id = ctx.getTask() == null ? ctx.getLoraToTrain().getName() : ctx.getTask().getTaskId();
                                statusResponse.set(inferenceService.getTaskStatus(new GetTaskDTO(id)));
                            } catch (Exception e) {
                                log.error("推理失败:{}", e.getMessage());
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
                                // 处理推理完成
                                if (Objects.equals(Objects.requireNonNull(statusResponse.get().body()).getStatus(), "完成")) {
                                    log.debug("推理完成:{}", statusResponse.get().body());
                                    statusTimer.cancel();

                                    // 释放锁
                                    if (!lockUtil.unlock("inference", "inference")) {
                                        log.error("释放锁失败");
                                    }

                                    // 更新任务状态
                                    updateTaskStatus(ctx, InferenceStatus.FINISHED);

                                    if (statusResponse.get() != null && statusResponse.get().body() != null) {
                                        if (ctx.getTaskType() == 1) {
                                            ctx.getTask().setResult(Objects.requireNonNull(statusResponse.get().body()).getResult());
                                            log.info("Result: {}", statusResponse.get().body().getResult());
                                            taskMapper.updateById(ctx.getTask());
                                        }
                                    } else {
                                        log.error("推理失败:{}", statusResponse.get().errorBody());
                                    }
                                    // 处理推理失败
                                } else if (Objects.equals(Objects.requireNonNull(statusResponse.get().body()).getStatus(), "运行失败")) {
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
    }

    private InferenceCtx initTask(FunctionPO function, List<LoraPO> loraList, TaskParamDTO dto) {
        // 生成taskId
        String taskId = UUIDUtil.generate();
        taskId = SHAUtil.MD5(taskId);
        log.info("生成taskId: {}", taskId);

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
        log.debug("生成message: {}", message);

        // 生成Cloth
        // 对于换装功能，需要获取cloth信息
        ClothPO cloth = null;
        if (dto.getClothId() != null) {
            cloth = clothMapper.selectById(dto.getClothId());
        }
        log.debug("生成cloth: {}", cloth);

        // 生成Task
        TaskPO task = null;
        LoraPO lora = null;
        if (dto.getTaskType() == 1) {
            task = TaskPO.builder()
                    .taskId(taskId)
                    .status(PENDING.getValue())
                    .functionId(Long.parseLong(dto.getFunction()))
                    .userId(UserContext.getUserId())
                    .messageId(message.getId())
                    .build();
            try {
                taskMapper.insert(task);
            } catch (Exception e) {
                log.error("initTask error", e);
            }
            log.debug("生成task: {}", task);
        } else if (dto.getTaskType() == 2) {
            lora = LoraPO.builder()
                    .userId(UserContext.getUserId())
                    .name(dto.getSceneId())
                    .images(dto.getSourceImages().toString())
                    .avatar(dto.getSourceImages().get(0))
                    .status(1)
                    .build();
            try {
                loraMapper.insert(lora);
            } catch (Exception e) {
                log.error("initTask error", e);
            }
            log.debug("生成lora: {}", lora);
        }

        // 构建推理上下文
        InferenceCtx ctx = InferenceCtx.builder()
                .taskType(dto.getTaskType())
                .task(task)
                .loraToTrain(lora)
                // .sceneId(dto.getSceneId())
                .status(PENDING)
                .function(function)
                .cloth(cloth)
                .message(message)
                .loras(loraList)
                .sourceImages(dto.getSourceImages())
                .build();
        log.debug("生成ctx: {}", ctx);

        log.info("任务初始化完成: {}", ctx);
        return ctx;
    }

    public void updateTaskStatus(InferenceCtx ctx, InferenceStatus status) {
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
        LoraPO lora = ctx.getLoraToTrain();
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

    /**
     * 推理请求
     *
     * <p>根据上下文中的任务类型，调用不同的推理接口
     *
     * @param ctx 上下文
     * @return 推理结果
     */
    private Response<Result<Void>> inferRequest(InferenceCtx ctx) {
        if (ctx.getTaskType() == 1) {
            FunctionPO function = ctx.getFunction();
            log.info("inferRequest: {}", function);
            if (Objects.equals(function.getType(), "solo")) {
                SingleLoraDTO dto = SingleLoraDTO.fromCtx(ctx);
                log.info("inferRequest: {}", dto);
                return inferenceService.singleLoraInfer(dto);
            } else if (Objects.equals(function.getType(), "multi")) {
                MultiLoraDTO dto = MultiLoraDTO.fromCtx(ctx);
                log.info("inferRequest: {}", dto);
                return inferenceService.multiLoraInfer(dto);
            } else if (Objects.equals(function.getType(), "scene")) {
                SceneDTO dto = SceneDTO.fromCtx(ctx);
                log.info("inferRequest: {}", dto);
                return inferenceService.sceneInfer(dto);
            } else if (Objects.equals(function.getType(), "video_scene")) {
                VideoDTO dto = VideoDTO.fromCtxToScene(ctx);
                log.info("inferRequest: {}", dto);
                return inferenceService.videoInfer(dto);
            } else if (Objects.equals(function.getType(), "video_solo")) {
                VideoDTO dto = VideoDTO.fromCtxToSolo(ctx);
                log.info("inferRequest: {}", dto);
                return inferenceService.videoInfer(dto);
            } else if (Objects.equals(function.getType(), "video_video")) {
                VideoDTO dto = VideoDTO.fromCtxToVideo(ctx);
                log.info("inferRequest: {}", dto);
                return inferenceService.videoInfer(dto);
            } else if (Objects.equals(function.getType(), "tryon")) {
                TryOnDTO dto = TryOnDTO.fromCtx(ctx);
                log.info("inferRequest: {}", dto);
                return inferenceService.tryOn(dto);
            } else if (Objects.equals(function.getType(), "anime")) {
                AnimeDTO dto = AnimeDTO.from(ctx);
                log.info("inferRequest: {}", dto);
                return inferenceService.animeInfer(dto);
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
