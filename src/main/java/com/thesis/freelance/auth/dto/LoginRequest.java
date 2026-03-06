package com.thesis.freelance.auth.dto;

import com.thesis.freelance.common.util.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username can only contain letters and numbers")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100)
    @ValidPassword(min = 8, max = 100)
    private String password;

}
