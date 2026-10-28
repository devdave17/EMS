
package com.IIITV.EMS.Services;

import com.IIITV.EMS.Model.QuestionBank;
import com.IIITV.EMS.Model.QuestionBankQuestion;
import com.IIITV.EMS.Model.Questions;
import com.IIITV.EMS.Model.Quiz;
import com.IIITV.EMS.Model.QuizStatus;
import com.IIITV.EMS.Repository.QuestionBankQuestionRepository;
import com.IIITV.EMS.Repository.QuestionBankRepository;
import com.IIITV.EMS.Repository.QuestionRepository;
import com.IIITV.EMS.Repository.QuizRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.IIITV.EMS.Model.QuestionBankQuestion;
@Service
@RequiredArgsConstructor
public class QuestionBankService {

    private final QuestionBankRepository questionBankRepository;
    private final QuestionBankQuestionRepository questionBankQuestionRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    public List<QuestionBankQuestion> getQuestionsFromBanks(
            List<String> bankIds,
            String teacherId) {

        if (bankIds == null || bankIds.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Select at least one question bank");
        }

        if (new HashSet<>(bankIds).size() != bankIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Duplicate question bank IDs are not allowed");
        }

        List<QuestionBankQuestion> result = new ArrayList<>();

        for (String bankId : bankIds) {

            // Verify that the bank exists, without checking ownership.
            QuestionBank bank = questionBankRepository.findById(bankId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Question bank not found: " + bankId));

            List<QuestionBankQuestion> questions =
                    questionBankQuestionRepository
                            .findByQuestionBankId(bank.getId());

            result.addAll(questions);
        }

        if (result.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selected question banks contain no questions");
        }

        return result;
    }
    // ---------- CREATE BANK ----------

    public QuestionBank createBank(QuestionBank bank, String teacherId) {
        requireTeacherId(teacherId);

        if (bank.getName() == null || bank.getName().isBlank()) {
            throw badRequest("Bank name is required");
        }

        if (bank.getSubject() == null || bank.getSubject().isBlank()) {
            throw badRequest("Subject is required");
        }

        bank.setId(null);
        bank.setName(bank.getName().trim());
        bank.setSubject(bank.getSubject().trim());
        bank.setCreatedBy(teacherId);

        LocalDateTime now = LocalDateTime.now();
        bank.setCreatedAt(now);
        bank.setUpdatedAt(now);

        return questionBankRepository.save(bank);
    }

    // ---------- GET TEACHER BANKS ----------

    public List<QuestionBank> getBanksByTeacher(String teacherId) {
        requireTeacherId(teacherId);
        return questionBankRepository.findByCreatedBy(teacherId);
    }

    // ---------- GET SINGLE BANK ----------

    public QuestionBank getBankById(String bankId, String teacherId) {
        QuestionBank bank = questionBankRepository.findById(bankId)
                .orElseThrow(() -> notFound("Question bank not found"));

        verifyOwnership(bank.getCreatedBy(), teacherId);
        return bank;
    }

    // ---------- UPDATE BANK ----------

    public QuestionBank updateBank(
            String bankId, QuestionBank updated, String teacherId) {

        QuestionBank bank = getBankById(bankId, teacherId);

        if (updated.getName() == null || updated.getName().isBlank()) {
            throw badRequest("Bank name is required");
        }

        if (updated.getSubject() == null || updated.getSubject().isBlank()) {
            throw badRequest("Subject is required");
        }

        bank.setName(updated.getName().trim());
        bank.setSubject(updated.getSubject().trim());
        bank.setDescription(updated.getDescription());
        bank.setUpdatedAt(LocalDateTime.now());

        return questionBankRepository.save(bank);
    }

    // ---------- DELETE BANK AND ITS BANK QUESTIONS ----------

    public void deleteBank(String bankId, String teacherId) {
        QuestionBank bank = getBankById(bankId, teacherId);

        questionBankQuestionRepository.deleteByQuestionBankId(bankId);
        questionBankRepository.delete(bank);
    }

    // ---------- ADD QUESTION TO BANK ----------

    public QuestionBankQuestion addQuestionToBank(
            String bankId,
            QuestionBankQuestion question,
            String teacherId) {

        QuestionBank bank = getBankById(bankId, teacherId);
        validateQuestion(question);

        question.setId(null);
        question.setQuestionBankId(bankId);
        question.setSubject(bank.getSubject());
        question.setCreatedBy(teacherId);

        LocalDateTime now = LocalDateTime.now();
        question.setCreatedAt(now);
        question.setUpdatedAt(now);

        return questionBankQuestionRepository.save(question);
    }

    // ---------- GET QUESTIONS IN BANK ----------

    public List<QuestionBankQuestion> getQuestionsInBank(
            String bankId, String teacherId) {

        getBankById(bankId, teacherId);

        return questionBankQuestionRepository
                .findByQuestionBankId(bankId);
    }

    // ---------- EXISTING INDIVIDUAL QUESTION METHODS ----------

    public QuestionBankQuestion createQuestion(
            QuestionBankQuestion question, String createdBy) {

        validateQuestion(question);

        question.setId(null);
        question.setCreatedBy(createdBy);

        LocalDateTime now = LocalDateTime.now();
        question.setCreatedAt(now);
        question.setUpdatedAt(now);

        return questionBankQuestionRepository.save(question);
    }

    public List<QuestionBankQuestion> getQuestionsBySubject(String subject) {
        if (subject == null || subject.isBlank()) {
            throw badRequest("Subject is required");
        }

        return questionBankQuestionRepository
                .findBySubjectIgnoreCase(subject.trim());
    }

    public QuestionBankQuestion getQuestionById(String id) {
        return questionBankQuestionRepository.findById(id)
                .orElseThrow(() -> notFound("Question not found"));
    }

    public QuestionBankQuestion updateQuestion(
            String id, QuestionBankQuestion updated) {

        QuestionBankQuestion existing = getQuestionById(id);
        validateQuestion(updated);

        existing.setSubject(updated.getSubject().trim());
        existing.setQuestionText(updated.getQuestionText().trim());
        existing.setOptions(new ArrayList<>(updated.getOptions()));
        existing.setCorrectOptionIndex(updated.getCorrectOptionIndex());
        existing.setPoints(updated.getPoints());
        existing.setUpdatedAt(LocalDateTime.now());

        return questionBankQuestionRepository.save(existing);
    }

    public void deleteQuestion(String id) {
        getQuestionById(id);
        questionBankQuestionRepository.deleteById(id);
    }

    // ---------- ADD BANK QUESTIONS TO A QUIZ ----------

    public List<Questions> selectQuestionsForQuiz(
            String quizId, List<String> questionBankIds) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> notFound("Quiz not found"));

        if (quiz.getStatus() != QuizStatus.DRAFT) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Questions can only be added to draft quizzes");
        }

        if (questionBankIds == null || questionBankIds.isEmpty()) {
            throw badRequest("Select at least one question");
        }

        if (new HashSet<>(questionBankIds).size() != questionBankIds.size()) {
            throw badRequest("Duplicate question IDs provided");
        }

        List<Questions> savedQuestions = new ArrayList<>();

        for (String questionId : questionBankIds) {
            QuestionBankQuestion source = getQuestionById(questionId);

            if (questionRepository.existsByQuizIdAndSourceBankQuestionId(
                    quizId, questionId)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Question already added to this quiz: " + questionId);
            }

            Questions question = new Questions();
            question.setQuizId(quizId);
            question.setSourceBankQuestionId(questionId);
            question.setQuestionText(source.getQuestionText());
            question.setOptions(new ArrayList<>(source.getOptions()));
            question.setCorrectOptionIndex(source.getCorrectOptionIndex());
            question.setPoints(source.getPoints());

            LocalDateTime now = LocalDateTime.now();
            question.setCreatedAt(now);
            question.setUpdatedAt(now);

            savedQuestions.add(questionRepository.save(question));
        }

        return savedQuestions;
    }

    // ---------- VALIDATION ----------

    private void validateQuestion(QuestionBankQuestion question) {
        if (question.getSubject() == null
                || question.getSubject().isBlank()
                || question.getSubject().trim().length() > 100) {
            throw badRequest("Valid subject is required (max 100 characters)");
        }

        if (question.getQuestionText() == null
                || question.getQuestionText().isBlank()
                || question.getQuestionText().trim().length() > 2000) {
            throw badRequest("Valid question text is required (max 2000 characters)");
        }

        if (question.getOptions() == null
                || question.getOptions().size() < 2
                || question.getOptions().size() > 6) {
            throw badRequest("A question must have 2 to 6 options");
        }

        for (String option : question.getOptions()) {
            if (option == null || option.isBlank() || option.length() > 500) {
                throw badRequest("Each option must be non-empty and max 500 characters");
            }
        }

        if (question.getCorrectOptionIndex() == null
                || question.getCorrectOptionIndex() < 0
                || question.getCorrectOptionIndex() >= question.getOptions().size()) {
            throw badRequest("Invalid correct option index");
        }

        if (question.getPoints() == null
                || question.getPoints().signum() <= 0) {
            throw badRequest("Points must be greater than zero");
        }
    }

    // ---------- HELPERS ----------

    private void requireTeacherId(String teacherId) {
        if (teacherId == null || teacherId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Teacher authentication required");
        }
    }

    private void verifyOwnership(String ownerId, String teacherId) {
        requireTeacherId(teacherId);

        if (!teacherId.equals(ownerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have access to this question bank");
        }
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    public List<String> getQuestionIdsFromBanks(
            List<String> bankIds,
            String teacherId) {

        if (bankIds == null || bankIds.isEmpty()) {
            throw badRequest("Select at least one question bank");
        }

        if (new HashSet<>(bankIds).size() != bankIds.size()) {
            throw badRequest("Duplicate question bank IDs are not allowed");
        }

        List<String> questionIds = new ArrayList<>();

        for (String bankId : bankIds) {
            getBankById(bankId, teacherId);

            List<QuestionBankQuestion> questions =
                    questionBankQuestionRepository.findByQuestionBankId(bankId);

            for (QuestionBankQuestion question : questions) {
                questionIds.add(question.getId());
            }
        }

        if (questionIds.isEmpty()) {
            throw badRequest("Selected banks contain no questions");
        }

        return questionIds;
    }
}
