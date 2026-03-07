package com.thesis.freelance.features.client.service;

import com.thesis.freelance.features.client.dto.ClientPersonalDto;
import com.thesis.freelance.features.client.dto.ClientPersonalUpdateDto;
import com.thesis.freelance.features.client.entity.Client;
import com.thesis.freelance.features.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientPersonalService {

    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public ClientPersonalDto getPersonal(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
        return toPersonalDto(client);
    }

    @Transactional
    public ClientPersonalDto updatePersonal(UUID clientId, ClientPersonalUpdateDto dto) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        if (dto.occupation() != null) client.setOccupation(dto.occupation());
        if (dto.companyName() != null) client.setCompanyName(dto.companyName());
        if (dto.bio() != null) client.setBio(dto.bio());
        if (dto.website() != null) client.setWebsite(dto.website());

        client.setCompleteness(calculateClientCompleteness(client));
        clientRepository.save(client);

        return toPersonalDto(client);
    }

    private int calculateClientCompleteness(Client client) {
        int filled = 0;
        int total = 4;

        if (client.getOccupation() != null && !client.getOccupation().isBlank()) filled++;
        if (client.getCompanyName() != null && !client.getCompanyName().isBlank()) filled++;
        if (client.getBio() != null && !client.getBio().isBlank()) filled++;
        if (client.getWebsite() != null && !client.getWebsite().isBlank()) filled++;

        return (filled * 100) / total;
    }

    private ClientPersonalDto toPersonalDto(Client client) {
        return new ClientPersonalDto(
                client.getId(),
                client.getOccupation(),
                client.getCompanyName(),
                client.getBio(),
                client.getWebsite(),
                client.getCompleteness()
        );
    }
}
