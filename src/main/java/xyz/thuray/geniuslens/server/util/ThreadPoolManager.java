package xyz.thuray.geniuslens.server.util;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
public class ThreadPoolManager {

    private final ThreadPoolTaskExecutor taskExecutor;

    public ThreadPoolManager() {
        taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(20);
        taskExecutor.setThreadNamePrefix("ThreadPool-");
        taskExecutor.initialize();
    }

    public Executor getTaskExecutor() {
        return taskExecutor;
    }
}
