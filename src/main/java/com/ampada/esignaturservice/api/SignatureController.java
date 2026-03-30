package com.ampada.esignaturservice.api;

import com.ampada.esignaturservice.model.dto.SignatureRequestDTO;
import com.ampada.esignaturservice.model.dto.SignatureResponseDTO;
import com.ampada.esignaturservice.service.SignatureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST-Controller für den eSignatur-Service.
 * Nimmt HTTP-Anfragen entgegen, delegiert an den Service und gibt die Antwort zurück.
 */
@RestController
@RequestMapping("/api/signatures")
@RequiredArgsConstructor
public class SignatureController {

    private final SignatureService service;

    /**
     * Neuen Signaturvorgang starten.
     * POST /api/signatures
     */
    @PostMapping
    public ResponseEntity<SignatureResponseDTO> createSignature(
            @Valid @RequestBody SignatureRequestDTO request) {
        SignatureResponseDTO response = service.createSignature(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Alle Signiervorgänge abrufen.
     * GET /api/signatures
     */
    @GetMapping
    public ResponseEntity<List<SignatureResponseDTO>> getAllSignatures() {
        return ResponseEntity.ok(service.getAllSignatures());
    }

    /**
     * Einzelnen Signaturvorgang anhand der ID abrufen.
     * GET /api/signatures/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<SignatureResponseDTO> getSignature(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getSignature(id));
    }

    /**
     * Signaturvorgang abbrechen.
     * DELETE /api/signatures/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelSignature(@PathVariable UUID id) {
        service.cancelSignature(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Erinnerung an den Unterzeichner senden.
     * POST /api/signatures/{id}/remind
     */
    @PostMapping("/{id}/remind")
    public ResponseEntity<Void> sendReminder(@PathVariable UUID id) {
        service.sendReminder(id);
        return ResponseEntity.ok().build();
    }
}
