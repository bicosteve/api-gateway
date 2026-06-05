package com.bicosteve.api_gateway.controllers;

import com.bicosteve.api_gateway.dto.requests.BetRequest;
import com.bicosteve.api_gateway.dto.response.*;
import com.bicosteve.api_gateway.service.BetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@SecurityRequirement(name="bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bet")
@Slf4j
@Tag(name="Bet Controller",description = "For bet related operations")
public class BetControllers{
    private final BetService betService;

    @PostMapping("/create")
    @Operation(
            summary="Create bet",
            description = "Used to create a bet and return the bet details")
    @ApiResponses(value={
            @ApiResponse(
                    responseCode = "201",
                    description = "Creat a bet with related slips",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BetResponse.class)
            )),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BadRequestResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServerErrorResponse.class)
                    )),

    })
    public ResponseEntity<BetResponse> bet(
            @Valid @RequestBody BetRequest request,
            Authentication authentication
        ){

        BetResponse bet = this.betService.placeBet(request, authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(bet);
    }


    @GetMapping("/{betId}")
    @Operation(summary = "Get One Bet", description = "Get a single bet using its bet_id")
    @ApiResponses(value={
            @ApiResponse(
                    responseCode = "200",
                    description = "Get a single bet with its related slips",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BetResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NotFoundResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServerErrorResponse.class)
                    )),

    })
    public ResponseEntity<BetResponse> getOneBet(
            @Parameter(
                    description = "Unique bet identifier",
                    example = "44",
                    required = true
            )
            @PathVariable Long betId,
            Authentication authentication
    ){
        return ResponseEntity.status(HttpStatus.OK).body(this.betService.getBet(betId,authentication));
    }


    @GetMapping("/all")
    @Operation(summary = "Get Bets")
    @ApiResponses(value={
            @ApiResponse(
                    responseCode = "200",
                    description = "Bets fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BetResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid filter or pagination parameter",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BadRequestResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing tokens",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BadRequestResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServerErrorResponse.class)
                    )),

    })
    public ResponseEntity<PageResponse<BetResponse>> getBets(
            @Parameter(
                    description = "Filter bets by period",
                    example="day | week | month | all"
            )
           @RequestParam(defaultValue = "all")
            @Pattern(
                    regexp="day|week|month|all",
                    message = "Filter must be one of: day, week, month,all")
            String filter,

            @Parameter(
                    description = "Page number starting from 0",
                    example="0"
            )
           @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be 0 or greater")
            int page,

            @Parameter(
                    description = "Number of records per page",
                    example="10"
            )
           @RequestParam(defaultValue = "10")
            @Min(value = 1, message="Size must be at least 1")
            @Max(value=50, message = "Size must not exceed 50")
            int size,
           Authentication auth
     ){
        PageResponse<BetResponse> bets = this.betService.getBets(filter, page, size, auth);
        return ResponseEntity.status(HttpStatus.OK).body(bets);
    }

}
