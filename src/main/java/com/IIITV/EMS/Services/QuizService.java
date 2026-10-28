package com.IIITV.EMS.Services;

import java.time.LocalDateTime;
import java.util.List;

import com.IIITV.EMS.Repository.QuestionBankQuestionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.IIITV.EMS.Model.Quiz;
import com.IIITV.EMS.Model.QuizStatus;
import com.IIITV.EMS.Repository.QuizRepository;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.IIITV.EMS.DTO.CreateExamRequest;
import com.IIITV.EMS.Model.QuestionBankQuestion;
import com.IIITV.EMS.Model.Questions;
import com.IIITV.EMS.Repository.QuestionRepository;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionService questionService;
    private final QuestionRepository questionRepository;
    private final QuestionBankService questionBankService;
    private final QuestionBankQuestionRepository questionBankQuestionRepository;
    public QuizService(
            QuizRepository quizRepository,
            QuestionService questionService, QuestionRepository questionRepository, QuestionBankService questionBankService, QuestionBankQuestionRepository questionBankQuestionRepository) {
        this.quizRepository = quizRepository;
        this.questionService = questionService;
        this.questionRepository = questionRepository;
        this.questionBankService = questionBankService;
        this.questionBankQuestionRepository = questionBankQuestionRepository;
    }
    public List<Quiz> getQuizzesByTeacher(String teacherId) {
        if (teacherId == null || teacherId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authenticated teacher is required"
            );
        }

        return quizRepository.findByCreatedBy(teacherId);
    }

    public Quiz updateMyQuiz(
            String quizId,
            Quiz updates,
            String teacherId
    ) {
        Quiz existing = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exam not found"
                ));

        verifyOwnership(existing.getCreatedBy(), teacherId);

        if (existing.getStatus() != QuizStatus.DRAFT) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only draft exams can be edited"
            );
        }

        if (updates.getTitle() == null
                || updates.getTitle().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Exam title is required"
            );
        }

        if (updates.getSubject() == null
                || updates.getSubject().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Subject is required"
            );
        }

        if (updates.getDurationMinutes() == null
                || updates.getDurationMinutes() < 1
                || updates.getDurationMinutes() > 600) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Duration must be between 1 and 600 minutes"
            );
        }

        if (updates.getStartTime() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Start time is required"
            );
        }

        existing.setTitle(updates.getTitle().trim());
        existing.setSubject(updates.getSubject().trim());
        existing.setDescription(updates.getDescription());
        existing.setDurationMinutes(updates.getDurationMinutes());
        existing.setStartTime(updates.getStartTime());

        existing.setEndTime(
                updates.getStartTime()
                        .plusMinutes(updates.getDurationMinutes())
        );

        existing.setUpdatedAt(LocalDateTime.now());

        return quizRepository.save(existing);
    }

    public void deleteMyQuiz(String quizId, String teacherId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exam not found"
                ));

        verifyOwnership(quiz.getCreatedBy(), teacherId);

        if (quiz.getStatus() != QuizStatus.DRAFT) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only draft exams can be deleted"
            );
        }

        // Remove the exam's associated questions first.
        questionRepository.deleteByQuizId(quizId);

        quizRepository.delete(quiz);
    }

    public Quiz updateMyQuizStatus(
            String quizId,
            QuizStatus status,
            String teacherId
    ) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exam not found"
                ));

        verifyOwnership(quiz.getCreatedBy(), teacherId);

        if (status == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status is required"
            );
        }

        // Reuse the existing transition and publishing validations.
        Quiz updatedQuiz = updateQuizStatus(quizId, status);

        updatedQuiz.setUpdatedAt(LocalDateTime.now());

        return quizRepository.save(updatedQuiz);
    }


    private void verifyOwnership(String ownerId, String teacherId) {
        if (teacherId == null
                || teacherId.isBlank()
                || ownerId == null
                || !ownerId.equals(teacherId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to manage this exam"
            );
        }
    }


    public Quiz createExamWithBanks(
            CreateExamRequest request,
            String teacherId) {

        if (request.getEndTime().isBefore(request.getStartTime())
                || request.getEndTime().isEqual(request.getStartTime())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End time must be after start time");
        }

        List<QuestionBankQuestion> bankQuestions =
                questionBankService.getQuestionsFromBanks(
                        request.getQuestionBankIds(),
                        teacherId);

        BigDecimal totalMarks = bankQuestions.stream()
                .map(QuestionBankQuestion::getPoints)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setSubject(request.getSubject());
        quiz.setDescription(request.getDescription());
        quiz.setDurationMinutes(request.getDurationMinutes());
        quiz.setStartTime(request.getStartTime());
        quiz.setEndTime(request.getEndTime());
        quiz.setTotalMarks(totalMarks);

        // Reuse existing logic to save a DRAFT quiz.
        Quiz savedQuiz = createQuiz(quiz, teacherId);

        List<String> questionIds = new ArrayList<>();

        for (QuestionBankQuestion question : bankQuestions) {
            questionIds.add(question.getId());
        }

        // Reuse the existing question-copy implementation.
        // This method must accept individual bank-question IDs.
        questionBankService.selectQuestionsForQuiz(
                savedQuiz.getId(),
                questionIds);

        return savedQuiz;
    }
    public Quiz createQuiz(Quiz quiz, String teacherId) {

        if (teacherId == null || teacherId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authenticated teacher is required"
            );
        }

        quiz.setId(null);
        quiz.setCreatedBy(teacherId);
        quiz.setStatus(QuizStatus.DRAFT);
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setUpdatedAt(LocalDateTime.now());

        return quizRepository.save(quiz);
    }

    public Quiz getQuizById(String id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Quiz not found"));
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

        if (quiz.getStatus() == status) {
            return quiz;
        }

        // Validate the exam whenever it is being published,
        // including reopening a previously closed exam.
        if (status == QuizStatus.PUBLISHED) {
            questionService.validateQuizForPublishing(id);
        }

        quiz.setStatus(status);
        quiz.setUpdatedAt(LocalDateTime.now());

        return quizRepository.save(quiz);
    }

}
