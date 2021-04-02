package org.example.trade.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableRetry
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
public class AppFrameworkConfiguration {

    @Bean
    public List<RetryListener> retryListeners() {
        Logger logger = LoggerFactory.getLogger(getClass());

        return Collections.singletonList(new RetryListener() {

            @Override
            public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
                // The 'context.name' attribute has not been set on the context yet. So we have to use reflection.
                Field labelField = ReflectionUtils.findField(callback.getClass(), "val$label");
                ReflectionUtils.makeAccessible(labelField);
                String label = (String) ReflectionUtils.getField(labelField, callback);
                logger.info("Starting retryable method {}", label);
                return true;
            }

            @Override
            public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
                                                         Throwable throwable) {
                logger.info("Retryable method {} threw {}th exception {}",
                         context.getAttribute("context.name"), context.getRetryCount(), throwable.toString());
            }

            @Override
            public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
                                                       Throwable throwable) {
                logger.info("Finished retryable method {}", context.getAttribute("context.name"));
            }
        });
    }

}
