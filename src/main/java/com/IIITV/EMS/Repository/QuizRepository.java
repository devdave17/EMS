
package com.IIITV.EMS.Repository;

import com.IIITV.EMS.Model.Quiz;
import com.IIITV.EMS.Model.QuizStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuizRepository extends MongoRepository<Quiz, String> {

    List<Quiz> findByStatus(QuizStatus status);
    long countByCreatedBy(String createdBy);
    List<Quiz> findByCreatedBy(String createdBy);
    long countByStatus(QuizStatus status);
}
