package com.IIITV.EMS.Services;

import com.IIITV.EMS.Model.Quiz;
import com.IIITV.EMS.Model.QuizStatus;
import com.IIITV.EMS.Repository.QuizRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public Quiz createQuiz(Quiz quiz) {
        quiz.setId(null);
        quiz.setStatus(QuizStatus.DRAFT);
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setUpdatedAt(LocalDateTime.now());

        return quizRepository.save(quiz);
    }

    public Quiz getQuizById(String id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Quiz not found"
                ));
    }

    public List<Quiz> getQuizzes(QuizStatus status) {
        if (status == null) {
            return quizRepository.findAll();
        }

        return quizRepository.findByStatus(status);
    }

    public Quiz updateQuizStatus(String id, QuizStatus status) {
        Quiz quiz = getQuizById(id);

        if (status == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status is required"
            );
        }

        QuizStatus currentStatus = quiz.getStatus();

        boolean validTransition =
                (currentStatus == QuizStatus.DRAFT
                        && status == QuizStatus.PUBLISHED)
                        || (currentStatus == QuizStatus.PUBLISHED
                        && status == QuizStatus.CLOSED);

        if (currentStatus == status) {
            return quiz;
        }

        if (!validTransition) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Invalid quiz status transition: "
                            + currentStatus + " -> " + status
            );
        }

        quiz.setStatus(status);
        quiz.setUpdatedAt(LocalDateTime.now());

        return quizRepository.save(quiz);
    }
}