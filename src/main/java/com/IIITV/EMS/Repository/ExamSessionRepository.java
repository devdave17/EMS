
package com.IIITV.EMS.Repository;

import com.IIITV.EMS.Model.ExamSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.List;

public interface ExamSessionRepository
        extends MongoRepository<ExamSession, String> {

    Optional<ExamSession> findByStudentIdAndQuizId(
            String studentId,
            String quizId
    );

    List<ExamSession> findByStudentId(String studentId);

    List<ExamSession> findByQuizId(String quizId);
}
