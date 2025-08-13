package com.example.momolearn.service;

import com.example.momolearn.model.Question;

import java.util.List;

public interface QuestionGenerator {
  List<Question> generate(String studySetId, String text, int count);
}
