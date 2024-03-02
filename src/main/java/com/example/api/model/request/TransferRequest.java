package com.example.api.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@Schema(description = "Transfer request")
public class TransferRequest {
    @Schema(description = "Amount", example = "234")
    @NotNull
    @DecimalMin(value = "0.1")
    private Double amount;
    @Schema(description = "Account number", example = "4444666688882222")
    @Length(min = 16, max = 16, message = "Account number must be exactly 16 characters")
    @NotNull
    private Long accountNumber;
}
