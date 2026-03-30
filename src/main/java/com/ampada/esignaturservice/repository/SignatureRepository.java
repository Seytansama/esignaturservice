package com.ampada.esignaturservice.repository;

import com.ampada.esignaturservice.model.entity.SignatureProcess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository für den Datenbankzugriff auf SignatureProcess-Entitäten.
 * Spring Data JPA stellt automatisch alle CRUD-Operationen bereit (save, findById, findAll, delete, ...).
 */
public interface SignatureRepository extends JpaRepository<SignatureProcess, UUID> {
    // Keine eigenen Methoden nötig – JpaRepository reicht für diesen Scope
}
