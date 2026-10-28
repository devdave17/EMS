package com.IIITV.EMS.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.IIITV.EMS.Model.QuestionBankQuestion;

public interface QuestionBankQuestionRepository
        extends MongoRepository<QuestionBankQuestion, String> {

    List<QuestionBankQuestion> findByQuestionBankId(String questionBankId);

    List<QuestionBankQuestion> findBySubjectIgnoreCase(String subject);

    List<QuestionBankQuestion> findByCreatedBy(String createdBy);

    long countByQuestionBankId(String questionBankId);

    void deleteByQuestionBankId(String questionBankId);
}