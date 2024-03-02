package com.example.api.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Customer's contact info request")
public class ContactInfoRequest {
    @Schema(description = "Phone number", example = "+7 9991234567")
    @Pattern(regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$", message = "Invalid phone number format")
    @Nullable
    private String phoneNumber;

    @Schema(description = "Additional phone number", example = "+7 9991234567")
    @Pattern(regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$", message = "Invalid phone number format")
    @Nullable
    private String newPhoneNumber;

    @Schema(description = "E-mail", example = "jondoe@gmail.com")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$", message = "Invalid email format")
    @Nullable
    private String email;

    @Schema(description = "E-mail", example = "jondoe@gmail.com")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$", message = "Invalid email format")
    @Nullable
    private String newEmail;
}
