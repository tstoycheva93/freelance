package com.thesis.freelance.features.client.web;

import com.thesis.freelance.features.client.dto.ClientPersonalDto;
import com.thesis.freelance.features.client.dto.ClientPersonalUpdateDto;
import com.thesis.freelance.features.client.service.ClientPersonalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/client/personal")
@RequiredArgsConstructor
public class ClientPersonalController {

    private final ClientPersonalService clientPersonalService;

    @GetMapping
    public ResponseEntity<?> getPersonal(@RequestParam UUID clientId) {
        return ResponseEntity.ok(clientPersonalService.getPersonal(clientId));
    }

    @PutMapping
    public ResponseEntity<?> updatePersonal(
            @RequestParam UUID clientId,
            @Valid @RequestBody ClientPersonalUpdateDto dto) {
        return ResponseEntity.ok(clientPersonalService.updatePersonal(clientId, dto));
    }
}
