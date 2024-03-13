//package xyz.thuray.geniuslens.server.service;
//
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//import retrofit2.Response;
//import xyz.thuray.geniuslens.server.data.context.InferenceCtx;
//import xyz.thuray.geniuslens.server.data.dto.sd.TaskStatusDTO;
//import xyz.thuray.geniuslens.server.data.enums.InferenceStatus;
//import xyz.thuray.geniuslens.server.service.GenerateService;
//import xyz.thuray.geniuslens.server.util.LockUtil;
//
//import java.util.Objects;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.atomic.AtomicReference;
//
//@Service
//@Slf4j
//public class InferenceProcessor {
//    @Resource
//    private LockUtil lockUtil;
//    @Resource
//    private GenerateService generateService;
//
//    private ScheduledExecutorService scheduler;
//    private final AtomicInteger count = new AtomicInteger(0);
//    private final AtomicReference<Response<TaskStatusDTO>> statusResponse = new AtomicReference<>();
//    private InferenceCtx ctx;
//
//    @KafkaListener(topics = {"inference"}, groupId = "group")
//    public void consumeMessage(ConsumerRecord<String, String> record) {
//        // Your existing code for consuming the Kafka message
//
//        // Initialize the ScheduledExecutorService
//        scheduler = Executors.newScheduledThreadPool(2);
//
//        // Schedule the initial task to acquire the lock
//        scheduler.scheduleAtFixedRate(this::acquireLock, 0, 1000, TimeUnit.MILLISECONDS);
//    }
//
//    private void acquireLock() {
//        if (!lockUtil.lock("inference", "inference")) {
//            count.getAndIncrement();
//        } else {
//            // Stop the current task
//            scheduler.shutdown();
//            log.info("获取锁成功 共尝试{}次", count);
//
//            // Your existing logic for processing the inference task
//
//            // Schedule a new task for polling the task status
//            scheduler = Executors.newScheduledThreadPool(1);
//            scheduler.scheduleAtFixedRate(this::pollTaskStatus, 0, 1000, TimeUnit.MILLISECONDS);
//        }
//    }
//
//    private void pollTaskStatus() {
//        try {
//            // Your existing logic for polling the task status
//
//            // Check if the inference is completed or failed
//            if (statusResponse.get() != null && statusResponse.get().body() != null) {
//                if (Objects.equals(Objects.requireNonNull(statusResponse.get().body()).getStatus(), "完成")) {
//                    // Inference completed
//                    handleInferenceCompletion();
//                } else if (Objects.equals(Objects.requireNonNull(statusResponse.get().body()).getStatus(), "运行失败")) {
//                    // Inference failed
//                    handleInferenceFailure();
//                }
//            }
//        } catch (Exception e) {
//            log.error("推理失败:{}", e.getMessage());
//            // Release the lock
//            if (!lockUtil.unlock("inference", "inference")) {
//                log.error("释放锁失败");
//            }
//            // Update task status
//            generateService.updateTaskStatus(ctx, InferenceStatus.FAILED);
//        }
//    }
//
//    private void handleInferenceCompletion() {
//        // Inference completed logic
//        // Release the lock
//        if (!lockUtil.unlock("inference", "inference")) {
//            log.error("释放锁失败");
//        }
//        // Update task status
//        generateService.updateTaskStatus(ctx, InferenceStatus.FINISHED);
//        // Add result
//        // Update task in the database
//    }
//
//    private void handleInferenceFailure() {
//        // Inference failed logic
//        // Release the lock
//        if (!lockUtil.unlock("inference", "inference")) {
//            log.error("释放锁失败");
//        }
//        // Update task status
//        generateService.updateTaskStatus(ctx, InferenceStatus.FAILED);
//    }
//}
