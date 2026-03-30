package com.ampada.esignaturservice.service;

import com.ampada.esignaturservice.adapter.demo.SignatureProviderAdapter;
import com.ampada.esignaturservice.model.dto.SignatureRequestDTO;
import com.ampada.esignaturservice.model.dto.SignatureResponseDTO;
import com.ampada.esignaturservice.model.entity.SignatureProcess;
import com.ampada.esignaturservice.repository.SignatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service-Klasse mit der gesamten Geschäftslogik des eSignatur-Services.
 * Koordiniert den Adapter (Provider) und das Repository (Datenbank).
 */
@Service
@RequiredArgsConstructor
public class SignatureService {

    private final SignatureProviderAdapter adapter;
    private final SignatureRepository repository;

    /**
     * Startet einen neuen Signaturvorgang.
     * 1. Adapter initiiert den Prozess beim Provider
     * 2. Vorgang wird mit Status PENDING in der DB gespeichert
     */
    public SignatureResponseDTO createSignature(SignatureRequestDTO request) {
        String providerProcessId = adapter.initiate(request);

        SignatureProcess process = SignatureProcess.builder()
                .provider(request.getProvider())
                .providerProcessId(providerProcessId)
                .documentRef(request.getDocumentRef())
                .signerName(request.getSignerName())
                .signerEmail(request.getSignerEmail())
                .status("PENDING")
                .build();

        SignatureProcess saved = repository.save(process);
        return toResponseDTO(saved);
    }

    /**
     * Gibt einen einzelnen Signaturvorgang anhand seiner ID zurück.
     * Wirft eine RuntimeException wenn die ID nicht existiert.
     */
    public SignatureResponseDTO getSignature(UUID id) {
        SignatureProcess process = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Signaturvorgang nicht gefunden mit ID: " + id));
        return toResponseDTO(process);
    }

    /**
     * Gibt alle gespeicherten Signiervorgänge zurück.
     */
    public List<SignatureResponseDTO> getAllSignatures() {
        return repository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Bricht einen Signaturvorgang ab und setzt den Status auf CANCELLED.
     */
    public void cancelSignature(UUID id) {
        SignatureProcess process = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Signaturvorgang nicht gefunden mit ID: " + id));

        adapter.cancel(process.getProviderProcessId());
        process.setStatus("CANCELLED");
        repository.save(process);
    }

    /**
     * Sendet eine Erinnerung an den Unterzeichner.
     * Nur möglich wenn der Vorgang noch aktiv ist (PENDING oder IN_PROGRESS).
     */
    public void sendReminder(UUID id) {
        SignatureProcess process = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Signaturvorgang nicht gefunden mit ID: " + id));

        if (!process.getStatus().equals("PENDING") && !process.getStatus().equals("IN_PROGRESS")) {
            throw new RuntimeException("Erinnerung nicht möglich – Vorgang hat Status: " + process.getStatus());
        }

        adapter.sendReminder(process.getProviderProcessId());
    }

    /**
     * Hilfsmethode: Wandelt eine Entity in ein Response-DTO um.
     */
    private SignatureResponseDTO toResponseDTO(SignatureProcess process) {
        return SignatureResponseDTO.builder()
                .id(process.getId())
                .provider(process.getProvider())
                .providerProcessId(process.getProviderProcessId())
                .documentRef(process.getDocumentRef())
                .signerName(process.getSignerName())
                .signerEmail(process.getSignerEmail())
                .status(process.getStatus())
                .createdAt(process.getCreatedAt())
                .updatedAt(process.getUpdatedAt())
                .build();
    }
}
