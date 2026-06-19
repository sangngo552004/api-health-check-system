package com.example.apihealthchecksystem.infrastructure.config;

import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class AsyncSchedulingConfig implements AsyncConfigurer {

  @Bean(name = "taskExecutor")
  public Executor taskExecutor(
      @Value("${app.async.executor.core-pool-size:2}") int corePoolSize,
      @Value("${app.async.executor.max-pool-size:4}") int maxPoolSize,
      @Value("${app.async.executor.queue-capacity:100}") int queueCapacity,
      @Value("${app.async.shutdown-await-seconds:10}") int shutdownAwaitSeconds) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(maxPoolSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setThreadNamePrefix("incident-async-");
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(shutdownAwaitSeconds);
    executor.initialize();
    return executor;
  }

  @Bean(name = "taskScheduler")
  public TaskScheduler taskScheduler(
      @Value("${app.scheduler.pool-size:1}") int poolSize,
      @Value("${app.async.shutdown-await-seconds:10}") int shutdownAwaitSeconds) {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(poolSize);
    scheduler.setThreadNamePrefix("health-scheduler-");
    scheduler.setWaitForTasksToCompleteOnShutdown(true);
    scheduler.setAwaitTerminationSeconds(shutdownAwaitSeconds);
    scheduler.initialize();
    return scheduler;
  }
}
