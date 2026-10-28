
package com.IIITV.EMS.Controller;

import com.IIITV.EMS.Model.QuestionBank;
import com.IIITV.EMS.Model.QuestionBankQuestion;
import com.IIITV.EMS.Model.Questions;
import com.IIITV.EMS.Services.QuestionBankService;
import com.IIITV.EMS.Security.JwtService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class QuestionBankController {

    private final QuestionBankService questionBankService;
    private final JwtService jwtService;

    // ---------- CREATE BANK ----------

    @PostMapping("/question-banks")
    public ResponseEntity<QuestionBank> createBank(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody QuestionBank bank) {

        String teacherId = requireTeacherOrAdmin(authorization);

        QuestionBank saved = questionBankService.createBank(bank, teacherId);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ---------- GET TEACHER'S BANKS ----------

    @GetMapping("/question-banks")
    public ResponseEntity<List<QuestionBank>> getMyBanks(
            @RequestHeader("Authorization") String authorization) {

        String teacherId = requireTeacherOrAdmin(authorization);

        return ResponseEntity.ok(
                questionBankService.getBanksByTeacher(teacherId));
    }

    // ---------- GET ONE BANK ----------

    @GetMapping("/question-banks/{bankId}")
    public ResponseEntity<QuestionBank> getBank(
            @PathVariable @NotBlank String bankId,
            @RequestHeader("Authorization") String authorization) {

        String teacherId = requireTeacherOrAdmin(authorization);

        return ResponseEntity.ok(
                questionBankService.getBankById(bankId, teacherId));
    }

    // ---------- UPDATE BANK ----------

    @PutMapping("/question-banks/{bankId}")
    public ResponseEntity<QuestionBank> updateBank(
            @PathVariable @NotBlank String bankId,
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody QuestionBank updatedBank) {

        String teacherId = requireTeacherOrAdmin(authorization);

        return ResponseEntity.ok(
                questionBankService.updateBank(
                        bankId, updatedBank, teacherId));
    }

    // ---------- DELETE BANK ----------

    @DeleteMapping("/question-banks/{bankId}")
    public ResponseEntity<Void> deleteBank(
            @PathVariable @NotBlank String bankId,
            @RequestHeader("Authorization") String authorization) {

        String teacherId = requireTeacherOrAdmin(authorization);

        questionBankService.deleteBank(bankId, teacherId);

        return ResponseEntity.noContent().build();
    }

    // ---------- ADD QUESTION TO BANK ----------

    @PostMapping("/question-banks/{bankId}/questions")
    public ResponseEntity<QuestionBankQuestion> addQuestion(
            @PathVariable @NotBlank String bankId,
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody QuestionBankQuestion question) {

        String teacherId = requireTeacherOrAdmin(authorization);

        QuestionBankQuestion saved =
                questionBankService.addQuestionToBank(
                        bankId, question, teacherId);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ---------- GET BANK QUESTIONS ----------

    @GetMapping("/question-banks/{bankId}/questions")
    public ResponseEntity<List<QuestionBankQuestion>> getBankQuestions(
            @PathVariable @NotBlank String bankId,
            @RequestHeader("Authorization") String authorization) {

        String teacherId = requireTeacherOrAdmin(authorization);

        return ResponseEntity.ok(
                questionBankService.getQuestionsInBank(bankId, teacherId));
    }

    // ---------- EXISTING INDIVIDUAL QUESTION ENDPOINTS ----------

    @PostMapping("/question-bank")
    public ResponseEntity<QuestionBankQuestion> createQuestion(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody QuestionBankQuestion question) {

        String teacherId = requireTeacherOrAdmin(authorization);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionBankService.createQuestion(question, teacherId));
    }

    @GetMapping("/question-bank")
    public ResponseEntity<List<QuestionBankQuestion>> getQuestionsBySubject(
            @RequestParam String subject,
            @RequestHeader("Authorization") String authorization) {

        requireTeacherOrAdmin(authorization);

        return ResponseEntity.ok(
                questionBankService.getQuestionsBySubject(subject));
    }

    @GetMapping("/question-bank/{id}")
    public ResponseEntity<QuestionBankQuestion> getQuestion(
            @PathVariable String id,
            @RequestHeader("Authorization") String authorization) {

        requireTeacherOrAdmin(authorization);

        return ResponseEntity.ok(questionBankService.getQuestionById(id));
    }

    @PutMapping("/question-bank/{id}")
    public ResponseEntity<QuestionBankQuestion> updateQuestion(
            @PathVariable String id,
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody QuestionBankQuestion question) {

        requireTeacherOrAdmin(authorization);

        return ResponseEntity.ok(
                questionBankService.updateQuestion(id, question));
    }

    @DeleteMapping("/question-bank/{id}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable String id,
            @RequestHeader("Authorization") String authorization) {

        requireTeacherOrAdmin(authorization);

        questionBankService.deleteQuestion(id);

        return ResponseEntity.noContent().build();
    }

    // ---------- SELECT BANK QUESTIONS FOR QUIZ ----------

    @PostMapping("/quizzes/{quizId}/questions/from-bank")
    public ResponseEntity<List<Questions>> selectQuestionsForQuiz(
            @PathVariable @NotBlank String quizId,
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody QuestionSelection selection) {

        requireTeacherOrAdmin(authorization);

        return ResponseEntity.ok(
                questionBankService.selectQuestionsForQuiz(
                        quizId, selection.questionBankIds()));
    }

    public record QuestionSelection(
            @NotEmpty List<@NotBlank String> questionBankIds) {
    }

    // ---------- JWT ROLE CHECK ----------

    private String requireTeacherOrAdmin(String authorization) {
        if (authorization == null
                || !authorization.startsWith("Bearer ")
                || authorization.substring(7).isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Bearer token required");
        }

        String token = authorization.substring(7);

        try {
            String role = jwtService.extractRole(token);

            if (!"TEACHER".equals(role) && !"ADMIN".equals(role)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Only teachers or admins can access question banks");
            }

            return jwtService.extractEmail(token);

        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }

    @PutMapping("/question-banks/{bankId}/questions/{questionId}")
    public ResponseEntity<?> updateBankQuestion(
            @PathVariable String bankId,
            @PathVariable String questionId,
            @Valid @RequestBody QuestionBankQuestion question,
            @RequestHeader("Authorization") String authorization) {

        String teacherId = requireTeacherOrAdmin(authorization);

        // Verify that the teacher owns this bank.
        questionBankService.getBankById(bankId, teacherId);

        // Verify that this question belongs to this bank.
        boolean belongsToBank = questionBankService
                .getQuestionsInBank(bankId, teacherId)
                .stream()
                .anyMatch(q -> questionId.equals(q.getId()));

        if (!belongsToBank) {
            return ResponseEntity.notFound().build();
        }

        questionBankService.updateQuestion(questionId, question);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/question-banks/{bankId}/questions/{questionId}")
    public ResponseEntity<?> deleteBankQuestion(
            @PathVariable String bankId,
            @PathVariable String questionId,
            @RequestHeader("Authorization") String authorization) {

        String teacherId = requireTeacherOrAdmin(authorization);

        questionBankService.getBankById(bankId, teacherId);

        boolean belongsToBank = questionBankService
                .getQuestionsInBank(bankId, teacherId)
                .stream()
                .anyMatch(q -> questionId.equals(q.getId()));

        if (!belongsToBank) {
            return ResponseEntity.notFound().build();
        }

        questionBankService.deleteQuestion(questionId);

        return ResponseEntity.noContent().build();
    }
}
