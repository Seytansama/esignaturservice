package com.ampada.esignaturservice.adapter.demo;

import com.ampada.esignaturservice.model.dto.SignatureRequestDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Demo-Implementierung des SignatureProviderAdapter.
 * Simuliert einen echten Signatur-Provider ohne externe API-Aufrufe.
 * Wird verwendet, um den gesamten Signing-Flow testbar zu machen.
 */
@Slf4j
public class DemoAdapter implements SignatureProviderAdapter {

    @Override
    public String initiate(SignatureRequestDTO request) {
        // Generiere eine zufällige UUID als Pseudo-Provider-Prozess-ID
        String providerProcessId = UUID.randomUUID().toString();
        log.info("Demo-Adapter: Signaturprozess gestartet für '{}' – Provider-ID: {}",
                request.getSignerEmail(), providerProcessId);
        return providerProcessId;
    }

    @Override
    public String getStatus(String providerProcessId) {
        // Im Demo-Modus gilt jeder Vorgang sofort als abgeschlossen
        log.info("Demo-Adapter: Status abgefragt für Provider-ID: {} → COMPLETED", providerProcessId);
        return "COMPLETED";
    }

    @Override
    public void cancel(String providerProcessId) {
        // Keine echte Aktion notwendig – nur Logging
        log.info("Demo-Adapter: Abbruch simuliert für Provider-ID: {}", providerProcessId);
    }

    @Override
    public void sendReminder(String providerProcessId) {
        // Keine echte E-Mail – nur Logging
        log.info("Demo-Adapter: Erinnerung simuliert für Provider-ID: {}", providerProcessId);
    }
}
