package com.ordina_assessment.word_count.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

/**
 * A Spring Component that listens for the ContextRefreshedEvent and logs all the available
 * endpoints within the Spring MVC application. This is helpful for debugging and ensuring all
 * expected endpoints are appropriately mapped.
 */
@Slf4j
@Component
public class EndpointsListener implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * Handles the ContextRefreshedEvent by logging all request mappings available
     * in the ApplicationContext. It lists each mapping with its corresponding handler method,
     * which is useful for verifying the active endpoints in the application.
     *
     * @param event the event indicating that the ApplicationContext has been initialized or refreshed.
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Logging endpoints at application startup.");
        ApplicationContext applicationContext = event.getApplicationContext();
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
                .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping
                .getHandlerMethods();

        // Iterate over the entry set of mappings and log the details
        map.forEach((key, value) -> log.info("Mapped \"{}\" to {}", key, value));
    }
}