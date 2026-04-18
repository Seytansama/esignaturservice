package com.ampada.esignaturservice.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integrationstests für den SignatureController.
 *
 * @SpringBootTest startet den vollständigen Spring-Kontext mit H2-Datenbank.
 * @AutoConfigureMockMvc stellt MockMvc bereit, ohne einen echten HTTP-Server zu starten.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SignatureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Gültiger Request-Body für alle POST-Tests
    private static final String GUELTIGER_REQUEST_BODY = """
            {
              "documentRef": "test-doc-001",
              "signerName": "Max Mustermann",
              "signerEmail": "max@example.com",
              "provider": "demo"
            }
            """;

    // -------------------------------------------------------
    // POST /api/signatures
    // -------------------------------------------------------

    /**
     * Ein gültiger POST-Request muss HTTP 201 zurückgeben
     * und einen Prozess mit Status PENDING anlegen.
     *
     * Hinweis: Der Controller gibt bewusst 201 CREATED zurück (nicht 200),
     * da eine neue Ressource erstellt wird – korrekt nach REST-Konvention.
     */
    @Test
    void postSignature_mitGueltigemBody_gibt201zurueck() throws Exception {
        mockMvc.perform(post("/api/signatures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(GUELTIGER_REQUEST_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.documentRef").value("test-doc-001"))
                .andExpect(jsonPath("$.signerName").value("Max Mustermann"))
                .andExpect(jsonPath("$.signerEmail").value("max@example.com"))
                .andExpect(jsonPath("$.provider").value("demo"));
    }

    // -------------------------------------------------------
    // GET /api/signatures/{id} – ID existiert
    // -------------------------------------------------------

    /**
     * Zuerst wird ein Prozess angelegt, dann mit der zurückgegebenen ID abgerufen.
     * Erwartet HTTP 200 und die korrekte ID im Response-Body.
     */
    @Test
    void getSignature_mitExistierenderID_gibt200zurueck() throws Exception {
        // Schritt 1: Prozess anlegen und ID aus der Antwort lesen
        MvcResult erstelltesErgebnis = mockMvc.perform(post("/api/signatures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(GUELTIGER_REQUEST_BODY))
                .andExpect(status().isCreated())
                .andReturn();

        String antwortJson = erstelltesErgebnis.getResponse().getContentAsString();
        JsonNode antwortKnoten = objectMapper.readTree(antwortJson);
        String id = antwortKnoten.get("id").asText();

        // Schritt 2: Prozess über ID abrufen
        mockMvc.perform(get("/api/signatures/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.documentRef").value("test-doc-001"));
    }

    // -------------------------------------------------------
    // GET /api/signatures/{id} – ID existiert nicht
    // -------------------------------------------------------

    /**
     * Eine zufällige UUID, die nicht in der Datenbank existiert,
     * muss HTTP 404 zurückgeben.
     */
    @Test
    void getSignature_mitNichtExistierenderID_gibt404zurueck() throws Exception {
        String nichtExistierendeId = UUID.randomUUID().toString();

        mockMvc.perform(get("/api/signatures/" + nichtExistierendeId))
                .andExpect(status().isNotFound());
    }
}
