package com.bicosteve.api_gateway.controllers;


import com.bicosteve.api_gateway.dto.response.*;
import com.bicosteve.api_gateway.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name="Event Controller", description = "Events Management Endpoint")
public class EventControllers{

    private final EventService eventService;

    @GetMapping("/all")
    @Operation(summary = "Get a list of upcoming events")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EventResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BadRequestResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServerErrorResponse.class)
                    )
            ),
    })
    public ResponseEntity<PageResponse<EventResponse>> showAllEvents(
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit,
            @RequestParam(defaultValue = "0") @Min(0) int offset){

        PageResponse<EventResponse> events = this.eventService.getEvents(limit,offset);
        return  ResponseEntity.status(HttpStatus.OK).body(events);
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Get one event")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Get one event with event_id",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EventResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NotFoundResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServerErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<EventResponse> showAnEvent(@PathVariable String eventId){
        EventResponse event = this.eventService.getEvent(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(event);
    }
}
