package com.bicosteve.api_gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TimeConfigTest {

    @Test
    void providesUtcClockBean() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TimeConfig.class)) {
            Clock clock = context.getBean(Clock.class);

            assertNotNull(clock);
            assertEquals("Z", clock.getZone().getId());
        }
    }
}
