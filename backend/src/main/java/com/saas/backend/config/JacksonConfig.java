package com.saas.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Configuration for Jackson JSON serialization.
 * Ensures Long values are serialized as Strings to maintain precision in
 * JavaScript.
 */
@Configuration
public class JacksonConfig {

    /**
     * Customizes the default ObjectMapper.
     * Registers a module to serialize Long values as Strings, preventing precision
     * issues
     * in JavaScript (where numbers are 64-bit floats).
     *
     * @param builder The Jackson2ObjectMapperBuilder.
     * @return The configured ObjectMapper bean.
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        SimpleModule module = new SimpleModule();
        // Serialize Longs to String to prevent precision loss in JS (max safe integer
        // is 2^53 - 1)
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);

        objectMapper.registerModule(module);
        return objectMapper;
    }
}
