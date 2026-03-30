package com.ampada.esignaturservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO für ausgehende Antworten an den Client.
 * Enthält alle Informationen eines Signiervorgangs – wird aus der Entity gemappt.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignatureResponseDTO {

    private UUID id;
    private String provider;
    private String providerProcessId;
    private String documentRef;
    private String signerName;
    private String signerEmail;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}