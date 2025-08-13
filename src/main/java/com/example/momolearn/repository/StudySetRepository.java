package com.example.momolearn.repository;

import com.example.momolearn.model.StudySet;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StudySetRepository extends MongoRepository<StudySet, String> {
    List<StudySet> findByCourseId(String courseId);
    List<StudySet> findByUserId(String userId);
    List<StudySet> findByUserIdAndCourseId(String userId, String courseId);
    Page<StudySet> findByUserIdAndCourseId(String userId, String courseId, Pageable pageable);
    List<StudySet> findAllByUserIdAndCourseId(String userId, String courseId);
    Optional<StudySet> findByIdAndUserIdAndCourseId(String id, String userId, String courseId);
    void deleteAllByUserIdAndCourseId(String userId, String courseId);
}