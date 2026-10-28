package com.IIITV.EMS.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.IIITV.EMS.Model.QuestionBank;

public interface QuestionBankRepository
        extends MongoRepository<QuestionBank, String> {

    List<QuestionBank> findByCreatedBy(String createdBy);

    List<QuestionBank> findByCreatedByAndSubjectIgnoreCase(
            String createdBy, String subject
    );
}