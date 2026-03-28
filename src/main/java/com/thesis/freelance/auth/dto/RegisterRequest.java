package com.thesis.freelance.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thesis.freelance.common.util.ValidPassword;
import com.thesis.freelance.common.util.ValidPhoneNumber;
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
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username can only contain letters and numbers")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @ValidPhoneNumber(message = "Invalid phone number. Use format: +[country code][number]")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100)
    @ValidPassword(min = 8, max = 100)
    private String password;

    @NotNull(message = "Birthdate is required")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthDate;

}
