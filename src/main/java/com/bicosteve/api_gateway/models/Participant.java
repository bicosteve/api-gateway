package com.bicosteve.api_gateway.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Participant{
    private Long            participantId;
    private Integer         rundownId;
    private String          type;
    private String          name;
    private Long            marketId;
    private LocalDateTime   createdAt;
    private LocalDateTime   updatedAt;

    private List<Price>     prices;
}
