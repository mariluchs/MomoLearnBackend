package com.example.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.backend.model.User;

public interface UserRepository extends MongoRepository<User, String> {
}
