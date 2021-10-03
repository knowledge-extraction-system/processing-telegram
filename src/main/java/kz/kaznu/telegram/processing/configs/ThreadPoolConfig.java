package kz.kaznu.telegram.processing.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration()
public class ThreadPoolConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor()  {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(200);
        executor.setMaxPoolSize(200);
        executor.setThreadNamePrefix("TextProcessingThread-");
        executor.initialize();
        return executor;
    }

}
