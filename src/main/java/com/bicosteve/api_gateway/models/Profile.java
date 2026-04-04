package com.bicosteve.api_gateway.models;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class Profile{
    private Long profileId;
    private String phoneNumber;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    // One profile has one set of settings
    private ProfileSettings profileSettings;
}
