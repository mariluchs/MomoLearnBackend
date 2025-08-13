package com.example.momolearn.repository;

import com.example.momolearn.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Collection;

public interface QuestionRepository extends MongoRepository<Question, String> {
    List<Question> findAllByStudySetId(String setId);
    long deleteByStudySetId(String studySetId);
    void deleteAllByStudySetIdIn(Collection<String> setIds);
}
