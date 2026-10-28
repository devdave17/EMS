package com.IIITV.EMS.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.IIITV.EMS.Model.Questions;

public interface QuestionRepository extends MongoRepository<Questions, String> {

    List<Questions> findByQuizId(String quizId);

    boolean existsByQuizIdAndSourceBankQuestionId(
            String quizId,
            String sourceBankQuestionId);
}
