package com.bicosteve.api_gateway.controllers;

import com.bicosteve.api_gateway.config.AppConfig;
import com.bicosteve.api_gateway.dto.response.ServerErrorResponse;
import com.bicosteve.api_gateway.dto.response.TestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/health")
@Slf4j
@Tag(name="Health Controller",description = "Checking the status of the api")
public class HealthCheck {
    private final AppConfig appConfig;

    @GetMapping("/test")
    @Operation(
            summary = "Test Api",
            description = "Used for health check"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Api is up and running",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TestResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServerErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<TestResponse> test(){
        TestResponse message = TestResponse.builder()
                .message("App running on port %s".formatted(this.appConfig.getPort()))
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}
