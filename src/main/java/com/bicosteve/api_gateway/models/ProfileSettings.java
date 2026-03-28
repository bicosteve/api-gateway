package com.bicosteve.api_gateway.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProfileSettings{
    private Long id;
    private Integer status;
    private Integer isVerified;
    private Integer isDeleted;
    private Long profileId;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
}
