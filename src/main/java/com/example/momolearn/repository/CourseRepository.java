package com.example.momolearn.repository;

import com.example.momolearn.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findByUserId(String userId);
    List<Course> findByTitleContainingIgnoreCase(String keyword);
    Page<Course> findByUserId(String userId, Pageable pageable);
    List<Course> findAllByUserId(String userId);
    java.util.Optional<Course> findByIdAndUserId(String id, String userId);
    void deleteByIdAndUserId(String id, String userId);

}
