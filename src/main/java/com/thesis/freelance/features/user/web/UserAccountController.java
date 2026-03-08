package com.thesis.freelance.features.user.web;

import com.thesis.freelance.features.user.dto.UserAccountDto;
import com.thesis.freelance.features.user.dto.UserAccountUpdateDto;
import com.thesis.freelance.features.user.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user/account")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    @GetMapping
    public ResponseEntity<?> getAccount(@RequestParam UUID userId) {
        return ResponseEntity.ok(userAccountService.getAccount(userId));
    }

    @PutMapping
    public ResponseEntity<?> updateAccount(
            @RequestParam UUID userId,
            @Valid @RequestBody UserAccountUpdateDto dto) {
        return ResponseEntity.ok(userAccountService.updateAccount(userId, dto));
    }
}
