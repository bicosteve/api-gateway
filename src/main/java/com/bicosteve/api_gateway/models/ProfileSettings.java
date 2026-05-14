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
public class ProfileSettings{
    private Long id;
    private Integer status;
    private Integer isVerified;
    private Integer isDeleted;
    private Long profileId;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
}
