
package com.IIITV.EMS.Controller;

import com.IIITV.EMS.DTO.CreateExamRequest;
import com.IIITV.EMS.Model.IdempotencyRecord;
import com.IIITV.EMS.Model.Quiz;
import com.IIITV.EMS.Model.QuizStatus;
import com.IIITV.EMS.Services.ExternalServiceClient;
import com.IIITV.EMS.Services.IdempotencyService;
import com.IIITV.EMS.Services.QuizService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;
    private final IdempotencyService idempotencyService;
    private final ExternalServiceClient externalServiceClient;

    public QuizController(
            QuizService quizService,
            IdempotencyService idempotencyService,
            ExternalServiceClient externalServiceClient
    ) {
        this.quizService = quizService;
        this.idempotencyService = idempotencyService;
        this.externalServiceClient = externalServiceClient;
    }

    // CREATE EXAM WITH QUESTION BANKS
    @PostMapping("/with-question-banks")
    public ResponseEntity<Quiz> createExamWithBanks(
            @RequestHeader("Idempotency-Key") String key,
            @Valid @RequestBody CreateExamRequest request
    ) {
        String teacherId = requireAuthenticatedTeacher();

        validateIdempotencyKey(key);

        String requestHash =
                idempotencyService.createRequestHash(request.toString());

        IdempotencyRecord existing = idempotencyService.find(key);

        if (existing != null) {
            if (!existing.getRequestHash().equals(requestHash)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Idempotency key reused with a different request"
                );
            }

            Quiz existingQuiz =
                    quizService.getQuizById(existing.getResourceId());

            if (!teacherId.equals(existingQuiz.getCreatedBy())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You cannot access this exam"
                );
            }

            return ResponseEntity.ok(existingQuiz);
        }

        Quiz savedQuiz =
                quizService.createExamWithBanks(request, teacherId);

        idempotencyService.save(
                key,
                requestHash,
                savedQuiz.getId()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/quizzes/{id}")
                .buildAndExpand(savedQuiz.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedQuiz);
    }

    // GET MY EXAMS
    @GetMapping("/mine")
    public ResponseEntity<List<Quiz>> getMyQuizzes() {
        String teacherId = requireAuthenticatedTeacher();

        return ResponseEntity.ok(
                quizService.getQuizzesByTeacher(teacherId)
        );
    }

    // EDIT MY DRAFT EXAM
    @PutMapping("/{id}/edit")
    public ResponseEntity<Quiz> editMyQuiz(
            @PathVariable String id,
            @Valid @RequestBody Quiz updates
    ) {
        String teacherId = requireAuthenticatedTeacher();

        return ResponseEntity.ok(
                quizService.updateMyQuiz(id, updates, teacherId)
        );
    }

    // UPDATE EXAM STATUS
    // Request body example: "PUBLISHED"
    @PutMapping("/{id}/status")
    public ResponseEntity<Quiz> updateQuizStatus(
            @PathVariable String id,
            @RequestBody QuizStatus status
    ) {
        String teacherId = requireAuthenticatedTeacher();

        return ResponseEntity.ok(
                quizService.updateMyQuizStatus(
                        id,
                        status,
                        teacherId
                )
        );
    }

    // DELETE MY DRAFT EXAM
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyQuiz(
            @PathVariable String id
    ) {
        String teacherId = requireAuthenticatedTeacher();

        quizService.deleteMyQuiz(id, teacherId);

        return ResponseEntity.noContent().build();
    }

    // CREATE A BASIC EXAM
    @PostMapping
    public ResponseEntity<Quiz> createQuiz(
            @RequestHeader("Idempotency-Key") String key,
            @Valid @RequestBody Quiz quiz
    ) {
        String teacherId = requireAuthenticatedTeacher();

        validateIdempotencyKey(key);

        String requestHash = idempotencyService.createRequestHash(
                quiz.getTitle() + "|" + quiz.getDescription()
        );

        IdempotencyRecord existing = idempotencyService.find(key);

        if (existing != null) {
            if (!existing.getRequestHash().equals(requestHash)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Idempotency key reused with a different request"
                );
            }

            Quiz existingQuiz =
                    quizService.getQuizById(existing.getResourceId());

            if (!teacherId.equals(existingQuiz.getCreatedBy())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You cannot access this exam"
                );
            }

            return ResponseEntity.ok(existingQuiz);
        }

        Quiz savedQuiz = quizService.createQuiz(quiz, teacherId);

        idempotencyService.save(
                key,
                requestHash,
                savedQuiz.getId()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/quizzes/{id}")
                .buildAndExpand(savedQuiz.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedQuiz);
    }

    // GET EXAM BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(
                quizService.getQuizById(id)
        );
    }

    // GET ALL EXAMS OR FILTER BY STATUS
    @GetMapping
    public ResponseEntity<List<Quiz>> getQuizzes(
            @RequestParam(required = false) QuizStatus status
    ) {
        return ResponseEntity.ok(
                quizService.getQuizzes(status)
        );
    }

    // COMMON AUTHENTICATION AND ROLE CHECK
    private String requireAuthenticatedTeacher() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal() == null
                || !(authentication.getPrincipal()
                instanceof String teacherId)
                || "anonymousUser".equals(teacherId)) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authenticated teacher is required"
            );
        }

        boolean isTeacher = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_TEACHER".equals(
                                authority.getAuthority()
                        )
                );

        if (!isTeacher) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only teachers can manage exams"
            );
        }

        return teacherId;
    }

    private void validateIdempotencyKey(String key) {
        if (key == null || key.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Idempotency-Key cannot be blank"
            );
        }
    }
}
