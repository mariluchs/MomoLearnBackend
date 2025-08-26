package com.example.momolearn.repository;

import com.example.momolearn.model.StudySet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Repository-Interface für die MongoDB-Collection "study_sets".
 *
 * Bietet Standard-CRUD-Methoden plus benutzerdefinierte Abfragen,
 * die Spring Data automatisch aus den Methodennamen generiert.
 */
public interface StudySetRepository extends MongoRepository<StudySet, String> {

    /**
     * Findet alle StudySets, die zu einem bestimmten Kurs gehören.
     *
     * @param courseId ID des Kurses
     * @return Liste der StudySets für diesen Kurs
     */
    List<StudySet> findByCourseId(String courseId);

    /**
     * Findet alle StudySets eines bestimmten Benutzers (kursübergreifend).
     *
     * @param userId ID des Benutzers
     * @return Liste aller StudySets dieses Benutzers
     */
    List<StudySet> findByUserId(String userId);

    /**
     * Findet alle StudySets eines bestimmten Benutzers in einem bestimmten Kurs.
     *
     * @param userId   ID des Benutzers
     * @param courseId ID des Kurses
     * @return Liste aller StudySets des Benutzers in diesem Kurs
     */
    List<StudySet> findByUserIdAndCourseId(String userId, String courseId);

    /**
     * Liefert eine paginierte Liste der StudySets eines Benutzers in einem bestimmten Kurs.
     *
     * @param userId   ID des Benutzers
     * @param courseId ID des Kurses
     * @param pageable Pageable-Objekt mit page, size und sort
     * @return Paginierte Liste der StudySets
     */
    Page<StudySet> findByUserIdAndCourseId(String userId, String courseId, Pageable pageable);

    /**
     * Alternative Methode, um alle StudySets eines Benutzers in einem Kurs zu finden.
     *
     * @param userId   ID des Benutzers
     * @param courseId ID des Kurses
     * @return Liste aller StudySets
     */
    List<StudySet> findAllByUserIdAndCourseId(String userId, String courseId);

    /**
     * Sucht ein StudySet anhand der ID, des Benutzers und des Kurses.
     * Nützlich für Sicherheitsprüfungen (Ownership).
     *
     * @param id       ID des StudySets
     * @param userId   ID des Benutzers
     * @param courseId ID des Kurses
     * @return Optional mit dem StudySet, falls vorhanden
     */
    Optional<StudySet> findByIdAndUserIdAndCourseId(String id, String userId, String courseId);

    /**
     * Löscht alle StudySets eines Benutzers in einem bestimmten Kurs.
     *
     * @param userId   ID des Benutzers
     * @param courseId ID des Kurses
     */
    void deleteAllByUserIdAndCourseId(String userId, String courseId);
}
