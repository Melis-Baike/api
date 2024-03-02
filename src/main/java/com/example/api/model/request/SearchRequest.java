package com.example.api.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.Data;

@Data
@Schema(description = "Customer search request")
public class SearchRequest {
    @Nullable
    @Schema(description = "Date of birth", example = "01-01-2001")
    private String dateOfBirth;
    @Nullable
    @Schema(description = "Phone number", example = "+7 9991234567")
    private String phoneNumber;
    @Nullable
    @Schema(description = "Full name", example = "Ivanov Ivan Ivanovich")
    private String fullName;
    @Nullable
    @Schema(description = "E-mail", example = "jondoe@gmail.com")
    private String email;

    @Schema(description = "Pageable")
    @Nullable
    private CustomPageable pageable;
}
