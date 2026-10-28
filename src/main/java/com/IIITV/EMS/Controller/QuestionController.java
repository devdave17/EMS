package com.IIITV.EMS.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.IIITV.EMS.Model.Questions;
import com.IIITV.EMS.Security.JwtService;
import com.IIITV.EMS.Services.QuestionService;
import com.IIITV.EMS.Services.QuestionService.StudentQuestion;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class QuestionController {

    private final QuestionService questionService;
    private final JwtService jwtService;

    public QuestionController(
            QuestionService questionService,
            JwtService jwtService) {
        this.questionService = questionService;
        this.jwtService = jwtService;
    }

    @PostMapping("/questions")
    public ResponseEntity<Questions> createQuestion(
            @Valid @RequestBody Questions question,
            @RequestHeader("Authorization") String authorization) {
        requireRole(authorization, "TEACHER", "ADMIN");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionService.createQuestion(question));
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<Questions> getQuestionById(
            @PathVariable String id,
            @RequestHeader("Authorization") String authorization) {
        requireRole(authorization, "TEACHER", "ADMIN");
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @GetMapping("/quizzes/{quizId}/questions")
    public ResponseEntity<List<Questions>> getQuestionsByQuizId(
            @PathVariable String quizId,
            @RequestHeader("Authorization") String authorization) {
        requireRole(authorization, "TEACHER", "ADMIN");
        return ResponseEntity.ok(questionService.getQuestionsByQuizId(quizId));
    }

    @GetMapping("/quizzes/{quizId}/questions/student")
    public ResponseEntity<List<StudentQuestion>> getStudentQuestions(
            @PathVariable String quizId,
            @RequestHeader("Authorization") String authorization) {
        requireRole(authorization, "STUDENT");
        return ResponseEntity.ok(questionService.getStudentQuestions(quizId));
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<Questions> updateQuestion(
            @PathVariable String id,
            @Valid @RequestBody Questions question,
            @RequestHeader("Authorization") String authorization) {
        requireRole(authorization, "TEACHER", "ADMIN");
        return ResponseEntity.ok(questionService.updateQuestion(id, question));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable String id,
            @RequestHeader("Authorization") String authorization) {
        requireRole(authorization, "TEACHER", "ADMIN");
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    private void requireRole(String authorization, String... allowedRoles) {
        if (authorization == null || !authorization.startsWith("Bearer ")
                || authorization.substring(7).isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Bearer token required");
        }

        String role;
        try {
            role = jwtService.extractRole(authorization.substring(7));
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid bearer token");
        }

        for (String allowedRole : allowedRoles) {
            if (allowedRole.equals(role)) {
                return;
            }
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Insufficient role for this operation");
    }
}
