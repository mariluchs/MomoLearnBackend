package com.example.momolearn.repository;

import com.example.momolearn.model.AnswerAttempt;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AnswerAttemptRepository extends MongoRepository<AnswerAttempt, String> {
  List<AnswerAttempt> findByUserIdOrderByCreatedAtDesc(String userId);
  long countByUserIdAndCorrectIsTrue(String userId);
}
