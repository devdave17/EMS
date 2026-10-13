package com.IIITV.EMS.Controller;

import com.IIITV.EMS.Model.IdempotencyRecord;
import com.IIITV.EMS.Model.Quiz;
import com.IIITV.EMS.Model.QuizStatus;
import com.IIITV.EMS.Services.ExternalServiceClient;
import com.IIITV.EMS.Services.IdempotencyService;
import com.IIITV.EMS.Services.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public QuizController(QuizService quizService, IdempotencyService idempotencyService, ExternalServiceClient externalServiceClient) {
        this.quizService = quizService;
        this.idempotencyService = idempotencyService;
        this.externalServiceClient = externalServiceClient;
    }


    @PostMapping
    public ResponseEntity<Quiz> createQuiz(
            @RequestHeader("Idempotency-Key") String key,
            @Valid @RequestBody Quiz quiz) {

        if (key.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Idempotency-Key cannot be blank"
            );
        }

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

            Quiz existingQuiz = quizService.getQuizById(
                    existing.getResourceId()
            );

            return ResponseEntity.ok(existingQuiz);
        }

        Quiz savedQuiz = quizService.createQuiz(quiz);

        idempotencyService.save(
                key,
                requestHash,
                savedQuiz.getId()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedQuiz.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedQuiz);
    }

    // 2. Get Quiz By ID
    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable String id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    // 3. Get All / Filtered Quizzes
    @GetMapping
    public ResponseEntity<List<Quiz>> getQuizzes(
            @RequestParam(required = false) QuizStatus status) {

        return ResponseEntity.ok(quizService.getQuizzes(status));
    }

    // 4. Update Quiz Status
    @PutMapping("/{id}/status")
    public ResponseEntity<Quiz> updateQuizStatus(
            @PathVariable String id,
            @RequestBody QuizStatus status) {

        return ResponseEntity.ok(
                quizService.updateQuizStatus(id, status)
        );
    }
}