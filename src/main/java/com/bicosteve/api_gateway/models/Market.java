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
public class Market{
    private Long            id;
    private Integer         marketRundownId;
    private Integer         marketTypeId;
    private Integer         periodId;
    private String          name;
    private String          description;
    private String          eventId;
    private LocalDateTime   createdAt;
    private LocalDateTime   updatedAt;

    private List<Participant> participants;
}
