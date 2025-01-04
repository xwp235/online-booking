package jp.onlinebooking.web.config;

import io.micrometer.common.lang.NonNullApi;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@NonNullApi
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Value("${spring.threads.virtual.enabled:off}")
    private boolean enableVirtualThread;

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return AsyncConfigurer.super.getAsyncUncaughtExceptionHandler();
    }

    @Bean
    Executor asyncExecutor(TaskExecutionProperties taskExecutionProperties) {
        var executor = new ThreadPoolTaskExecutor();
        var poolConfig = taskExecutionProperties.getPool();
        var shutdownConfig = taskExecutionProperties.getShutdown();
        executor.setCorePoolSize(poolConfig.getCoreSize());
        executor.setMaxPoolSize(poolConfig.getMaxSize());
        executor.setQueueCapacity(poolConfig.getQueueCapacity());
        executor.setKeepAliveSeconds(Math.toIntExact(poolConfig.getKeepAlive().toSeconds()));
        executor.setThreadNamePrefix(taskExecutionProperties.getThreadNamePrefix());
        executor.setWaitForTasksToCompleteOnShutdown(shutdownConfig.isAwaitTermination());
        executor.setAwaitTerminationSeconds(Math.toIntExact(shutdownConfig.getAwaitTerminationPeriod().toSeconds()));
        executor.setVirtualThreads(enableVirtualThread);
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        executor.setTaskDecorator(new AsyncTaskDecorator());
        executor.initialize();
        return executor;
    }

    public static class AsyncTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            // メインスレッドからコンテキストデータを取得
            var contextMap = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    if (Objects.nonNull(contextMap)) {
                        // サブスレッドが実行される前に、現在のサブスレッドのコンテキストにデータを設定
                        MDC.setContextMap(contextMap);
                    } else {
                        MDC.clear();
                    }
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        }
    }

}
