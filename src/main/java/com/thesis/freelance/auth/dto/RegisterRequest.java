package com.thesis.freelance.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class RegisterRequest {


    @NotNull(message = "Role is required")
    private UUID roleId;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100)
    private String surname;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username can only contain letters and numbers")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Phone number must be in E.164 format"
    )
    private String phone;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Birthdate is required")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthDate;

}
