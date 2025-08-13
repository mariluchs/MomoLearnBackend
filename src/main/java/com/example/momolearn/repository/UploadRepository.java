package com.example.momolearn.repository;

import com.example.momolearn.model.UploadDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UploadRepository extends MongoRepository<UploadDoc, String> {}
