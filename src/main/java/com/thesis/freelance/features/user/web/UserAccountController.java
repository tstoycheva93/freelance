package com.thesis.freelance.features.user.web;

import com.thesis.freelance.features.user.dto.UserAccountUpdateDto;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import com.thesis.freelance.features.user.service.UserAccountService;
import com.thesis.freelance.features.user.service.UserSecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/account")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final UserSecurityService userSecurityService;

    @GetMapping
    public ResponseEntity<?> getAccount(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userSecurityService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userAccountService.getAccount(user.getId()));
    }

    @PutMapping
    public ResponseEntity<?> updateAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserAccountUpdateDto dto) {
        User user = userSecurityService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userAccountService.updateAccount(user.getId(), dto));
    }
}
