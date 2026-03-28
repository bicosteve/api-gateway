package com.bicosteve.api_gateway.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participant{
    private Long            participantId;
    private Integer         rundownId;
    private String          type;
    private String          name;
    private Long            marketId;
    private LocalDateTime   createdAt;
    private LocalDateTime   updatedAt;
}
