package com.bicosteve.api_gateway.config;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Jackson configuration for handling OffsetDateTime serialization.
 * 
 * OPTION 1: application.yml (simplest - uncomment in application.yml)
 * OPTION 2: Custom ObjectMapper Bean (enabled below)
 * OPTION 3: Convert to UTC in DTOs (see EventDtoMapper)
 * OPTION 4: Custom serializer (see CustomOffsetDateTimeSerializer)
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register JavaTimeModule for Java 8 date/time support
        mapper.registerModule(new JavaTimeModule());
        
        // OPTION 2A: Disable writing dates as timestamps (ISO-8601 format)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // OPTION 2B: Configure date format globally
        //mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"));

        // Ensure timezone information is preserved from the OffsetDateTime
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);


        return mapper;
    }
}

