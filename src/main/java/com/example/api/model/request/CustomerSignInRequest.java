package com.example.api.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Customer sign in request")
public class CustomerSignInRequest {
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
}
