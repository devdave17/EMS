package com.IIITV.EMS.Services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.IIITV.EMS.Model.Questions;
import com.IIITV.EMS.Model.Quiz;
import com.IIITV.EMS.Model.QuizStatus;
import com.IIITV.EMS.Repository.QuestionRepository;
import com.IIITV.EMS.Repository.QuizRepository;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    public QuestionService(
            QuestionRepository questionRepository,
            QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }

    public Questions createQuestion(Questions question) {
        if (question == null) {
            throw badRequest("Question is required");
        }

        requireDraftQuiz(question.getQuizId());
        validateQuestion(question);

        LocalDateTime now = LocalDateTime.now();
        question.setId(null);
        question.setCreatedAt(now);
        question.setUpdatedAt(now);
        return questionRepository.save(question);
    }

    public Questions getQuestionById(String id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Question not found"));
    }

    public List<Questions> getQuestionsByQuizId(String quizId) {
        requireQuiz(quizId);
        return questionRepository.findByQuizId(quizId);
    }

    public List<StudentQuestion> getStudentQuestions(String quizId) {
        Quiz quiz = requireQuiz(quizId);
        if (quiz.getStatus() != QuizStatus.PUBLISHED) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Published quiz not found");
        }

        return getQuestionsByQuizId(quizId).stream()
                .map(question -> new StudentQuestion(
                        question.getId(),
                        question.getQuestionText(),
                        question.getOptions(),
                        question.getPoints()))
                .toList();
    }

    public Questions updateQuestion(String id, Questions updatedQuestion) {
        if (updatedQuestion == null) {
            throw badRequest("Question is required");
        }

        Questions existingQuestion = getQuestionById(id);
        requireDraftQuiz(existingQuestion.getQuizId());

        if (updatedQuestion.getQuizId() != null
                && !updatedQuestion.getQuizId().equals(existingQuestion.getQuizId())) {
            throw badRequest("A question cannot be moved to another quiz");
        }

        updatedQuestion.setQuizId(existingQuestion.getQuizId());
        validateQuestion(updatedQuestion);

        existingQuestion.setQuestionText(updatedQuestion.getQuestionText());
        existingQuestion.setOptions(updatedQuestion.getOptions());
        existingQuestion.setCorrectOptionIndex(
                updatedQuestion.getCorrectOptionIndex());
        existingQuestion.setPoints(updatedQuestion.getPoints());
        existingQuestion.setUpdatedAt(LocalDateTime.now());
        return questionRepository.save(existingQuestion);
    }

    public void deleteQuestion(String id) {
        Questions question = getQuestionById(id);
        requireDraftQuiz(question.getQuizId());
        questionRepository.delete(question);
    }

    public void validateQuizForPublishing(String quizId) {
        requireQuiz(quizId);
        List<Questions> questions = questionRepository.findByQuizId(quizId);
        if (questions.isEmpty()) {
            throw badRequest("A quiz must contain at least one question before publishing");
        }

        questions.forEach(this::validateQuestion);
    }

    private Quiz requireQuiz(String quizId) {
        if (quizId == null || quizId.isBlank()) {
            throw badRequest("Quiz ID is required");
        }

        return quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Quiz not found"));
    }

    private Quiz requireDraftQuiz(String quizId) {
        Quiz quiz = requireQuiz(quizId);
        if (quiz.getStatus() != QuizStatus.DRAFT) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Questions can only be changed while the quiz is a draft");
        }
        return quiz;
    }

    private void validateQuestion(Questions question) {
        if (question.getQuestionText() == null
                || question.getQuestionText().isBlank()
                || question.getQuestionText().length() > 2000) {
            throw badRequest("Question text must contain 1 to 2000 characters");
        }

        List<String> options = question.getOptions();
        if (options == null || options.size() < 2 || options.size() > 6
                || options.stream().anyMatch(option -> option == null
                || option.isBlank() || option.length() > 500)) {
            throw badRequest("A question must have 2 to 6 nonblank options of at most 500 characters");
        }

        Integer correctOptionIndex = question.getCorrectOptionIndex();
        if (correctOptionIndex == null || correctOptionIndex < 0
                || correctOptionIndex >= options.size()) {
            throw badRequest("Correct option index must match one of the question options");
        }

        BigDecimal points = question.getPoints();
        if (points == null || points.compareTo(BigDecimal.ZERO) <= 0) {
            throw badRequest("Question points must be greater than zero");
        }
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    public record StudentQuestion(
            String id,
            String questionText,
            List<String> options,
            BigDecimal points) {
    }
}
