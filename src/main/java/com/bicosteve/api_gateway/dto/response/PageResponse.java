package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Paginated response wrapper")
public class PageResponse<T> {

    @Schema(description = "List of items")
    private List<T> data;

    @Schema(description = "Current page number", example="0")
    private int page;

    @Schema(description = "Number of items per page", example="10")
    private int limit;

    @Schema(description = "Whether there is a next page", example="true")
    private boolean hasNext;

    @Schema(description = "Whether there is a previous page", example="false")
    private boolean hasPrevious;
}
