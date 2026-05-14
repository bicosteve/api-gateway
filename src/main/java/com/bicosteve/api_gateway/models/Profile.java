package com.bicosteve.api_gateway.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Profile{
    private Long profileId;
    private String phoneNumber;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    // One profile has one set of settings
    private ProfileSettings profileSettings;
}
