package es.jose.bizumjose.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String fullName,
        @NotBlank @Pattern(regexp = "\\+?[0-9]{7,15}") String phoneNumber
) {}
