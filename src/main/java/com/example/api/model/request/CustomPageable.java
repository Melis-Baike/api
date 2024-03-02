package com.example.api.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Pageable")
public class CustomPageable {
    @NotNull
    @Min(0)
    private int page = 0;
    @NotNull
    @Min(1)
    private int size = 2;
}
