package com.ampada.esignaturservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO für eingehende Signaturanfragen vom Client.
 * Alle Felder sind Pflichtfelder und werden per Bean Validation geprüft.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignatureRequestDTO {

    @NotBlank(message = "Dokumentenreferenz darf nicht leer sein")
    private String documentRef;

    @NotBlank(message = "Name des Unterzeichners darf nicht leer sein")
    private String signerName;

    @NotBlank(message = "E-Mail des Unterzeichners darf nicht leer sein")
    private String signerEmail;

    // Standardmäßig wird der Demo-Provider verwendet
    private String provider = "demo";
}
