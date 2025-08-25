package com.example.momolearn.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.momolearn.model.SessionToken;

public interface SessionTokenRepository extends MongoRepository<SessionToken, String> {
  Optional<SessionToken> findByToken(String token);
  void deleteByToken(String token);
}
