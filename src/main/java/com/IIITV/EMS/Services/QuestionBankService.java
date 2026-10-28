package com.IIITV.EMS.Services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.IIITV.EMS.Model.QuestionBankQuestion;
import com.IIITV.EMS.Model.Questions;
import com.IIITV.EMS.Model.Quiz;
import com.IIITV.EMS.Model.QuizStatus;
import com.IIITV.EMS.Repository.QuestionBankRepository;
import com.IIITV.EMS.Repository.QuestionRepository;
import com.IIITV.EMS.Repository.QuizRepository;

@Service
public class QuestionBankService {

    private final QuestionBankRepository questionBankRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    public QuestionBankService(
            QuestionBankRepository questionBankRepository,
            QuestionRepository questionRepository,
            QuizRepository quizRepository) {
        this.questionBankRepository = questionBankRepository;
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }

    public QuestionBankQuestion createQuestion(
            QuestionBankQuestion question,
            String createdBy) {
        validateQuestion(question);
        question.setId(null);
        question.setSubject(question.getSubject().trim());
        question.setCreatedBy(createdBy);
        LocalDateTime now = LocalDateTime.now();
        question.setCreatedAt(now);
        question.setUpdatedAt(now);
        return questionBankRepository.save(question);
    }

    public List<QuestionBankQuestion> getQuestionsBySubject(String subject) {
        if (subject == null || subject.isBlank()) {
            throw badRequest("Subject is required");
        }
        return questionBankRepository.findBySubjectIgnoreCase(subject.trim());
    }

    public QuestionBankQuestion getQuestionById(String id) {
        return questionBankRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Question bank entry not found"));
    }

    public QuestionBankQuestion updateQuestion(
            String id,
            QuestionBankQuestion updatedQuestion) {
        validateQuestion(updatedQuestion);
        QuestionBankQuestion existingQuestion = getQuestionById(id);

        existingQuestion.setSubject(updatedQuestion.getSubject().trim());
        existingQuestion.setQuestionText(updatedQuestion.getQuestionText());
        existingQuestion.setOptions(new ArrayList<>(updatedQuestion.getOptions()));
        existingQuestion.setCorrectOptionIndex(
                updatedQuestion.getCorrectOptionIndex());
        existingQuestion.setPoints(updatedQuestion.getPoints());
        existingQuestion.setUpdatedAt(LocalDateTime.now());
        return questionBankRepository.save(existingQuestion);
    }

    public void deleteQuestion(String id) {
        questionBankRepository.delete(getQuestionById(id));
    }

    public List<Questions> selectQuestionsForQuiz(
            String quizId,
            List<String> questionBankIds) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Quiz not found"));
        if (quiz.getStatus() != QuizStatus.DRAFT) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Questions can only be added while the quiz is a draft");
        }
        if (questionBankIds == null || questionBankIds.isEmpty()) {
            throw badRequest("At least one question bank ID is required");
        }

        Set<String> uniqueIds = new HashSet<>(questionBankIds);
        if (uniqueIds.size() != questionBankIds.size()
                || uniqueIds.contains(null) || uniqueIds.contains("")) {
            throw badRequest("Question bank IDs must be nonblank and unique");
        }

        List<QuestionBankQuestion> bankQuestions = new ArrayList<>();
        for (String bankQuestionId : questionBankIds) {
            QuestionBankQuestion bankQuestion = getQuestionById(bankQuestionId);
            if (questionRepository.existsByQuizIdAndSourceBankQuestionId(
                    quizId, bankQuestionId)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Question is already selected for this quiz: " + bankQuestionId);
            }
            bankQuestions.add(bankQuestion);
        }

        LocalDateTime now = LocalDateTime.now();
        List<Questions> quizQuestions = bankQuestions.stream()
                .map(bankQuestion -> toQuizQuestion(
                        quizId, bankQuestion, now))
                .toList();
        return questionRepository.saveAll(quizQuestions);
    }

    private Questions toQuizQuestion(
            String quizId,
            QuestionBankQuestion bankQuestion,
            LocalDateTime now) {
        Questions question = new Questions();
        question.setQuizId(quizId);
        question.setSourceBankQuestionId(bankQuestion.getId());
        question.setQuestionText(bankQuestion.getQuestionText());
        question.setOptions(new ArrayList<>(bankQuestion.getOptions()));
        question.setCorrectOptionIndex(bankQuestion.getCorrectOptionIndex());
        question.setPoints(bankQuestion.getPoints());
        question.setCreatedAt(now);
        question.setUpdatedAt(now);
        return question;
    }

    private void validateQuestion(QuestionBankQuestion question) {
        if (question == null
                || question.getSubject() == null
                || question.getSubject().isBlank()
                || question.getSubject().length() > 100) {
            throw badRequest("Subject must contain 1 to 100 characters");
        }
        if (question.getQuestionText() == null
                || question.getQuestionText().isBlank()
                || question.getQuestionText().length() > 2000) {
            throw badRequest("Question text must contain 1 to 2000 characters");
        }

        List<String> options = question.getOptions();
        if (options == null || options.size() < 2 || options.size() > 6
                || options.stream().anyMatch(option -> option == null
                || option.isBlank() || option.length() > 500)) {
            throw badRequest(
                    "A question must have 2 to 6 nonblank options of at most 500 characters");
        }

        Integer correctOptionIndex = question.getCorrectOptionIndex();
        if (correctOptionIndex == null || correctOptionIndex < 0
                || correctOptionIndex >= options.size()) {
            throw badRequest(
                    "Correct option index must match one of the question options");
        }

        BigDecimal points = question.getPoints();
        if (points == null || points.compareTo(BigDecimal.ZERO) <= 0) {
            throw badRequest("Question points must be greater than zero");
        }
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
