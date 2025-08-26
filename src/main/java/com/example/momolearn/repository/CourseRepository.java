package com.example.momolearn.repository;

import com.example.momolearn.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Repository-Interface für die MongoDB-Collection "courses".
 *
 * Bietet Standard-CRUD-Operationen und zusätzliche Query-Methoden,
 * die über die Namenskonvention von Spring Data automatisch umgesetzt werden.
 */
public interface CourseRepository extends MongoRepository<Course, String> {

    /**
     * Liefert alle Kurse eines bestimmten Benutzers.
     *
     * @param userId ID des Benutzers
     * @return Liste mit allen Kursen des Benutzers
     */
    List<Course> findByUserId(String userId);

    /**
     * Sucht alle Kurse, deren Titel den angegebenen Text enthält (Case-insensitive).
     *
     * @param keyword Suchbegriff
     * @return Liste passender Kurse
     */
    List<Course> findByTitleContainingIgnoreCase(String keyword);

    /**
     * Liefert eine paginierte Liste der Kurse eines Benutzers.
     *
     * @param userId   ID des Benutzers
     * @param pageable Pageable-Objekt (enthält page, size, sort)
     * @return Page mit Kursen
     */
    Page<Course> findByUserId(String userId, Pageable pageable);

    /**
     * Liefert alle Kurse eines Benutzers (Alternative zu findByUserId).
     */
    List<Course> findAllByUserId(String userId);

    /**
     * Holt einen bestimmten Kurs anhand der ID und des Besitzers.
     *
     * @param id     ID des Kurses
     * @param userId ID des Benutzers
     * @return Optional mit dem Kurs, falls gefunden
     */
    Optional<Course> findByIdAndUserId(String id, String userId);

    /**
     * Löscht einen Kurs anhand der ID und des Besitzers.
     *
     * @param id     ID des Kurses
     * @param userId ID des Benutzers
     */
    void deleteByIdAndUserId(String id, String userId);
}
