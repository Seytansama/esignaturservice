package com.ampada.esignaturservice.adapter;

import com.ampada.esignaturservice.adapter.demo.DemoAdapter;
import com.ampada.esignaturservice.model.dto.SignatureRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests für den DemoAdapter.
 *
 * Der DemoAdapter hat keine Abhängigkeiten zu anderen Klassen,
 * daher brauchen wir hier keine Mocks – wir testen direkt.
 */
class DemoAdapterTest {

    // Das Objekt, das wir testen wollen
    private DemoAdapter demoAdapter;

    // Wird vor jedem Test aufgerufen – erstellt einen frischen Adapter
    @BeforeEach
    void setUp() {
        demoAdapter = new DemoAdapter();
    }

    // -------------------------------------------------------
    // initiate() Tests
    // -------------------------------------------------------

    @Test
    void initiate_gibtEineGueltigeUUID_zurueck() {
        // Vorbereitung: Eine einfache Anfrage
        SignatureRequestDTO anfrage = new SignatureRequestDTO(
                "doc-001", "Max Mustermann", "max@beispiel.de", "demo"
        );

        // Ausführung
        String ergebnis = demoAdapter.initiate(anfrage);

        // Prüfung: Das Ergebnis darf nicht leer sein
        assertNotNull(ergebnis);
        assertFalse(ergebnis.isEmpty());

        // Prüfung: Das Ergebnis muss eine gültige UUID sein
        // UUID.fromString() wirft eine Exception wenn das Format falsch ist
        assertDoesNotThrow(() -> java.util.UUID.fromString(ergebnis));
    }

    @Test
    void initiate_gibtJedesmalEineAndereUUID_zurueck() {
        // Vorbereitung
        SignatureRequestDTO anfrage = new SignatureRequestDTO(
                "doc-002", "Erika Muster", "erika@beispiel.de", "demo"
        );

        // Ausführung: Zweimal aufrufen
        String ersteId = demoAdapter.initiate(anfrage);
        String zweiteId = demoAdapter.initiate(anfrage);

        // Prüfung: Beide UUIDs müssen unterschiedlich sein (zufällig generiert)
        assertNotEquals(ersteId, zweiteId);
    }

    // -------------------------------------------------------
    // getStatus() Tests
    // -------------------------------------------------------

    @Test
    void getStatus_gibtImmerCOMPLETED_zurueck() {
        // Ausführung: Mit einer beliebigen Provider-ID aufrufen
        String status = demoAdapter.getStatus("irgendeine-provider-id");

        // Prüfung: Der Demo-Adapter gibt immer COMPLETED zurück
        assertEquals("COMPLETED", status);
    }

    @Test
    void getStatus_gibtAuchMitLeererID_COMPLETED_zurueck() {
        // Prüfung: Auch mit leerem String funktioniert es
        String status = demoAdapter.getStatus("");

        assertEquals("COMPLETED", status);
    }

    // -------------------------------------------------------
    // cancel() Tests
    // -------------------------------------------------------

    @Test
    void cancel_wirdOhneException_ausgefuehrt() {
        // Prüfung: cancel() darf keine Exception werfen
        assertDoesNotThrow(() -> demoAdapter.cancel("provider-123"));
    }

    // -------------------------------------------------------
    // sendReminder() Tests
    // -------------------------------------------------------

    @Test
    void sendReminder_wirdOhneException_ausgefuehrt() {
        // Prüfung: sendReminder() darf keine Exception werfen
        assertDoesNotThrow(() -> demoAdapter.sendReminder("provider-456"));
    }
}
