package com.thesis.freelance.features.client.web;

import com.thesis.freelance.features.client.dto.ClientPersonalDto;
import com.thesis.freelance.features.client.dto.ClientPersonalUpdateDto;
import com.thesis.freelance.features.client.entity.Client;
import com.thesis.freelance.features.client.repository.ClientRepository;
import com.thesis.freelance.features.client.service.ClientPersonalService;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client/personal")
@RequiredArgsConstructor
public class ClientPersonalController {

    private final ClientPersonalService clientPersonalService;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    @GetMapping
    public ResponseEntity<?> getPersonal(@AuthenticationPrincipal UserDetails userDetails) {
        Client client = getClient(userDetails);
        return ResponseEntity.ok(clientPersonalService.getPersonal(client.getId()));
    }

    @PutMapping
    public ResponseEntity<?> updatePersonal(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ClientPersonalUpdateDto dto) {
        Client client = getClient(userDetails);
        return ResponseEntity.ok(clientPersonalService.updatePersonal(client.getId(), dto));
    }

    private Client getClient(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return clientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Client profile not found"));
    }
}
