package com.bicosteve.api_gateway.controllers;

import com.bicosteve.api_gateway.dto.response.BadRequestResponse;
import com.bicosteve.api_gateway.dto.response.ProfileResponse;
import com.bicosteve.api_gateway.dto.response.ServerErrorResponse;
import com.bicosteve.api_gateway.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name="bearerAuth")
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name="Profile Controller",description = "For profile related operations")
public class ProfileController {
    private final ProfileService profileService;


    @GetMapping("/me")
    @Operation(
            summary="Get user profile",
            description = "Returns user profile details")
    @ApiResponses(value={
            @ApiResponse(
                    responseCode = "200",
                    description = "Return user details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfileResponse.class)
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
    public ResponseEntity<ProfileResponse> getMe(Authentication auth) {
        ProfileResponse profile = this.profileService.getProfileById(auth);
        return ResponseEntity.status(HttpStatus.OK).body(profile);
    }
}
