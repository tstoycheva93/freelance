package com.thesis.freelance.features.user.web;

import com.thesis.freelance.features.user.dto.ChangePasswordDto;
import com.thesis.freelance.features.user.service.UserSecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user/security")
@RequiredArgsConstructor
public class UserSecurityController {

    private final UserSecurityService userSecurityService;

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @RequestParam UUID userId,
            @Valid @RequestBody ChangePasswordDto dto) {
        userSecurityService.changePassword(userId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount(@RequestParam UUID userId) {
        userSecurityService.deleteAccount(userId);
        return ResponseEntity.ok().build();
    }
}
