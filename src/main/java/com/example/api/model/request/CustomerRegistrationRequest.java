package com.example.api.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Customer registration request")
public class CustomerRegistrationRequest {
    @Schema(description = "Login", example = "myLogin")
    @NotNull(message = "Login can't be null")
    @NotBlank(message = "Login can't be blank")
    @Size(max = 50, message = "Login exceeds the length limit (50)")
    private String login;
    @Schema(description = "Password", example = "myPassword1")
    @Size(min = 8, max = 255, message = "The password length must be from 8 to 255 characters")
    @NotNull(message = "Password can't be null")
    @NotBlank(message = "Password can't be blank")
    private String password;
    @Schema(description = "Full name", example = "Ivanov Ivan Ivanovich")
    @NotNull(message = "Full name can't be null")
    @NotBlank(message = "Full name can't be blank")
    @Size(max = 255, message = "Full name exceeds the length limit (255)")
    private String fullName;

    @Schema(description = "E-mail", example = "jondoe@gmail.com")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$", message = "Invalid email format")
    private String email;
    @Schema(description = "Phone number", example = "+7 9991234567")
    @Pattern(regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$", message = "Invalid phone number format")
    private String phoneNumber;

    @Schema(description = "Date of birth", example = "01-01-2001")
    @Pattern(regexp = "^\\d{2}-\\d{2}-\\d{4}$", message = "Date of birth must be in the format 'dd-MM-yyyy'")
    private String dateOfBirth;

    @Schema(description = "Initial deposit", example = "102.1")
    @NotNull
    @DecimalMin(value = "0.1")
    private Double initialAmount;
}
