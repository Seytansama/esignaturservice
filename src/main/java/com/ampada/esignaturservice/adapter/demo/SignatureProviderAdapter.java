package com.ampada.esignaturservice.adapter.demo;

import com.ampada.esignaturservice.model.dto.SignatureRequestDTO;

/**
 * Adapter-Interface für externe Signatur-Provider.
 * Jeder Provider (Demo, DocuSign, Adobe Sign, ...) muss dieses Interface implementieren.
 * So bleibt der Service unabhängig vom konkreten Provider – das ist das Adapter-Pattern.
 */
public interface SignatureProviderAdapter {

    /**
     * Startet einen neuen Signaturprozess beim Provider.
     *
     * @return die providerInterne Prozess-ID
     */
    String initiate(SignatureRequestDTO request);

    /**
     * Fragt den aktuellen Status eines Vorgangs beim Provider ab.
     *
     * @return Status als String (z.B. "COMPLETED")
     */
    String getStatus(String providerProcessId);

    /**
     * Bricht einen laufenden Signaturvorgang beim Provider ab.
     */
    void cancel(String providerProcessId);

    /**
     * Sendet eine Erinnerungsmail an den Unterzeichner.
     */
    void sendReminder(String providerProcessId);
}
