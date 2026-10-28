package com.IIITV.EMS.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.IIITV.EMS.Model.QuestionBankQuestion;

public interface QuestionBankRepository
        extends MongoRepository<QuestionBankQuestion, String> {

    List<QuestionBankQuestion> findBySubjectIgnoreCase(String subject);
}
