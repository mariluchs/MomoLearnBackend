package com.example.momolearn.repository;

import com.example.momolearn.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Collection;

/**
 * Repository-Interface für die MongoDB-Collection "questions".
 *
 * Bietet Standard-CRUD-Methoden plus spezielle Query-Methoden für Fragen,
 * die zu einem bestimmten StudySet gehören.
 */
public interface QuestionRepository extends MongoRepository<Question, String> {

    /**
     * Holt alle Fragen, die zu einem bestimmten StudySet gehören.
     *
     * @param setId ID des StudySets
     * @return Liste aller Fragen dieses Sets
     */
    List<Question> findAllByStudySetId(String setId);

    /**
     * Löscht alle Fragen eines bestimmten StudySets.
     *
     * @param studySetId ID des StudySets
     * @return Anzahl der gelöschten Dokumente
     */
    long deleteByStudySetId(String studySetId);

    /**
     * Löscht alle Fragen, die zu einer Menge von StudySets gehören.
     *
     * @param setIds Liste mit IDs mehrerer StudySets
     */
    void deleteAllByStudySetIdIn(Collection<String> setIds);
}
