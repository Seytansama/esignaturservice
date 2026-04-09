package com.ampada.esignaturservice.service;

import com.ampada.esignaturservice.adapter.demo.SignatureProviderAdapter;
import com.ampada.esignaturservice.model.dto.SignatureRequestDTO;
import com.ampada.esignaturservice.model.dto.SignatureResponseDTO;
import com.ampada.esignaturservice.model.entity.SignatureProcess;
import com.ampada.esignaturservice.repository.SignatureRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests für den SignatureService.
 *
 * Wir testen hier die Geschäftslogik des Services.
 * Adapter und Repository werden durch Mocks ersetzt –
 * so brauchen wir keine echte Datenbank oder externe API.
 */
@ExtendWith(MockitoExtension.class)
class SignatureServiceTest {

    // Mock-Objekte: Mockito erstellt Dummy-Versionen dieser Klassen
    @Mock
    private SignatureProviderAdapter adapter;

    @Mock
    private SignatureRepository repository;

    // Der echte Service – Mockito fügt die Mocks automatisch ein
    @InjectMocks
    private SignatureService signatureService;

    // -------------------------------------------------------
    // createSignature Tests
    // -------------------------------------------------------

    @Test
    void createSignature_gibtAntwortMitStatusPending_zurueck() {
        // Vorbereitung: Wir bauen eine Anfrage und definieren das Verhalten der Mocks
        SignatureRequestDTO anfrage = new SignatureRequestDTO(
                "doc-001", "Max Mustermann", "max@beispiel.de", "demo"
        );

        // Der Adapter soll eine feste Provider-ID zurückgeben
        when(adapter.initiate(any())).thenReturn("provider-123");

        // Das Repository soll den gespeicherten Prozess zurückgeben
        SignatureProcess gespeicherterProzess = SignatureProcess.builder()
                .id(UUID.randomUUID())
                .provider("demo")
                .providerProcessId("provider-123")
                .documentRef("doc-001")
                .signerName("Max Mustermann")
                .signerEmail("max@beispiel.de")
                .status("PENDING")
                .build();
        when(repository.save(any())).thenReturn(gespeicherterProzess);

        // Ausführung: Service-Methode aufrufen
        SignatureResponseDTO antwort = signatureService.createSignature(anfrage);

        // Prüfung: Das Ergebnis muss korrekt sein
        assertNotNull(antwort);
        assertEquals("PENDING", antwort.getStatus());
        assertEquals("doc-001", antwort.getDocumentRef());
        assertEquals("Max Mustermann", antwort.getSignerName());

        // Prüfung: Der Adapter und das Repository wurden genau einmal aufgerufen
        verify(adapter, times(1)).initiate(any());
        verify(repository, times(1)).save(any());
    }

    // -------------------------------------------------------
    // getSignature Tests
    // -------------------------------------------------------

    @Test
    void getSignature_gibtVorgangMitAktuellemStatus_zurueck() {
        // Vorbereitung: Ein vorhandener Prozess in der Datenbank
        UUID id = UUID.randomUUID();
        SignatureProcess prozessInDB = SignatureProcess.builder()
                .id(id)
                .provider("demo")
                .providerProcessId("provider-456")
                .documentRef("doc-002")
                .signerName("Erika Muster")
                .signerEmail("erika@beispiel.de")
                .status("PENDING")
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(prozessInDB));
        when(adapter.getStatus("provider-456")).thenReturn("COMPLETED");
        when(repository.save(any())).thenReturn(prozessInDB);

        // Ausführung
        SignatureResponseDTO antwort = signatureService.getSignature(id);

        // Prüfung: Status wurde vom Adapter aktualisiert
        assertNotNull(antwort);
        assertEquals("COMPLETED", antwort.getStatus());
        assertEquals(id, antwort.getId());
    }

    @Test
    void getSignature_wirftException_wennIdNichtExistiert() {
        // Vorbereitung: Repository gibt nichts zurück
        UUID unbekannteId = UUID.randomUUID();
        when(repository.findById(unbekannteId)).thenReturn(Optional.empty());

        // Prüfung: Es muss eine RuntimeException geworfen werden
        assertThrows(RuntimeException.class, () -> signatureService.getSignature(unbekannteId));
    }

    // -------------------------------------------------------
    // cancelSignature Tests
    // -------------------------------------------------------

    @Test
    void cancelSignature_setzt_Status_auf_CANCELLED() {
        // Vorbereitung: Ein aktiver Prozess in der Datenbank
        UUID id = UUID.randomUUID();
        SignatureProcess aktiverProzess = SignatureProcess.builder()
                .id(id)
                .providerProcessId("provider-789")
                .status("PENDING")
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(aktiverProzess));
        when(repository.save(any())).thenReturn(aktiverProzess);

        // Ausführung
        signatureService.cancelSignature(id);

        // Prüfung: Der Status muss auf CANCELLED gesetzt worden sein
        assertEquals("CANCELLED", aktiverProzess.getStatus());

        // Prüfung: Der Adapter wurde mit der richtigen Provider-ID aufgerufen
        verify(adapter, times(1)).cancel("provider-789");
    }

    @Test
    void cancelSignature_wirftException_wennIdNichtExistiert() {
        // Vorbereitung: Prozess existiert nicht
        UUID unbekannteId = UUID.randomUUID();
        when(repository.findById(unbekannteId)).thenReturn(Optional.empty());

        // Prüfung: Exception wird geworfen
        assertThrows(RuntimeException.class, () -> signatureService.cancelSignature(unbekannteId));
    }

    // -------------------------------------------------------
    // getAllSignatures Tests
    // -------------------------------------------------------

    @Test
    void getAllSignatures_gibtAlleProzesse_zurueck() {
        // Vorbereitung: Zwei Prozesse in der Datenbank
        SignatureProcess prozess1 = SignatureProcess.builder()
                .id(UUID.randomUUID()).status("PENDING").provider("demo")
                .documentRef("doc-A").signerName("Hans").signerEmail("hans@test.de")
                .build();
        SignatureProcess prozess2 = SignatureProcess.builder()
                .id(UUID.randomUUID()).status("COMPLETED").provider("demo")
                .documentRef("doc-B").signerName("Anna").signerEmail("anna@test.de")
                .build();

        when(repository.findAll()).thenReturn(List.of(prozess1, prozess2));

        // Ausführung
        List<SignatureResponseDTO> ergebnis = signatureService.getAllSignatures();

        // Prüfung: Es müssen genau 2 Einträge zurückkommen
        assertEquals(2, ergebnis.size());
    }
}
