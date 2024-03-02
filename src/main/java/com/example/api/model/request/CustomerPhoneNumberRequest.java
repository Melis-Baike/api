package com.example.api.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Customer's phone number request")
public class CustomerPhoneNumberRequest {
    @Schema(description = "Customer id")
    @NotNull(message = "Customer id can't be null")
    @NotBlank(message = "Customer id can't be blank")
    private Long customerId;
    @Schema(description = "Phone number", example = "+7 9991234567")
    @Pattern(regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$", message = "Invalid phone number format")
    private String phoneNumber;
}
