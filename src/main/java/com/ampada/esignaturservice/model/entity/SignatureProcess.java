package com.ampada.esignaturservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Datenbankentität für einen Signaturvorgang.
 * Speichert alle relevanten Informationen zu einem laufenden oder abgeschlossenen Signierprozess.
 */
@Entity
@Table(name = "signature_processes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignatureProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Name des genutzten Providers (z.B. "demo")
    private String provider;

    // ID des Vorgangs beim externen Provider
    private String providerProcessId;

    // Referenz auf das zu signierende Dokument
    private String documentRef;

    private String signerName;
    private String signerEmail;

    // Mögliche Werte: PENDING, IN_PROGRESS, COMPLETED, FAILED, CANCELLED
    private String status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}