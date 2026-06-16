package com.social_media.apigateway.config;

import io.micrometer.context.ContextRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration
public class PropagationConfig {

    @PostConstruct
    public void init() {
        // 1. Cho phép Reactor tự động copy context sang MDC
        Hooks.enableAutomaticContextPropagation();

        ContextRegistry.getInstance().registerThreadLocalAccessor(
                "correlationId",                              // Tham số 1: Key
                () -> MDC.get("correlationId"),               // Tham số 2: Getter
                val -> MDC.put("correlationId", (String) val),// Tham số 3: Setter
                () -> MDC.remove("correlationId")             // Tham số 4: Clear
        );
    }
}
