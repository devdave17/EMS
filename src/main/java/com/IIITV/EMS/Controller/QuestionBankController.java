package com.IIITV.EMS.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.IIITV.EMS.Model.QuestionBankQuestion;
import com.IIITV.EMS.Model.Questions;
import com.IIITV.EMS.Security.JwtService;
import com.IIITV.EMS.Services.QuestionBankService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@RestController
@Validated
@RequestMapping("/api")
public class QuestionBankController {

    private final QuestionBankService questionBankService;
    private final JwtService jwtService;

    public QuestionBankController(
            QuestionBankService questionBankService,
            JwtService jwtService) {
        this.questionBankService = questionBankService;
        this.jwtService = jwtService;
    }

    @PostMapping("/question-bank")
    public ResponseEntity<QuestionBankQuestion> createQuestion(
            @Valid @RequestBody QuestionBankQuestion question,
            @RequestHeader("Authorization") String authorization) {
        String createdBy = requireTeacherOrAdmin(authorization);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionBankService.createQuestion(question, createdBy));
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
            @Valid @RequestBody QuestionBankQuestion question,
            @RequestHeader("Authorization") String authorization) {
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

    @PostMapping("/quizzes/{quizId}/questions/from-bank")
    public ResponseEntity<List<Questions>> selectQuestionsForQuiz(
            @PathVariable String quizId,
            @Valid @RequestBody QuestionSelection selection,
            @RequestHeader("Authorization") String authorization) {
        requireTeacherOrAdmin(authorization);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                questionBankService.selectQuestionsForQuiz(
                        quizId, selection.questionBankIds()));
    }

    private String requireTeacherOrAdmin(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")
                || authorization.substring(7).isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Bearer token required");
        }

        String token = authorization.substring(7);
        String role;
        try {
            role = jwtService.extractRole(token);
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid bearer token");
        }

        if (!"TEACHER".equals(role) && !"ADMIN".equals(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only teachers and administrators can manage question bank entries");
        }

        try {
            return jwtService.extractEmail(token);
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid bearer token");
        }
    }

    public record QuestionSelection(
            @NotEmpty List<@NotBlank String> questionBankIds) {
    }
}
