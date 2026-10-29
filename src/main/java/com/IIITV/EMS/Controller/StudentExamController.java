
package com.IIITV.EMS.Controller;

import com.IIITV.EMS.Model.ExamSession;
import com.IIITV.EMS.Model.Questions;
import com.IIITV.EMS.Repository.QuestionRepository;
import com.IIITV.EMS.Services.ExamSessionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/student/exams")
public class StudentExamController {

    private final ExamSessionService examSessionService;
    private final QuestionRepository questionRepository;

    public StudentExamController(
            ExamSessionService examSessionService,
            QuestionRepository questionRepository) {
        this.examSessionService = examSessionService;
        this.questionRepository = questionRepository;
    }
    public record WarningCountRequest(Integer totalWarnings) {}

    @PutMapping("/sessions/{sessionId}/warnings")
    public ResponseEntity<Void> saveWarningCount(
            @PathVariable String sessionId,
            @RequestBody WarningCountRequest request) {

        String studentId = requireAuthenticatedStudent();

        if (request == null || request.totalWarnings() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Warning count is required");
        }

        examSessionService.saveWarningCount(
                sessionId, studentId, request.totalWarnings());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{quizId}/start")
    public ResponseEntity<ExamSessionResponse> startOrResumeExam(
            @PathVariable String quizId) {

        String studentId = requireAuthenticatedStudent();

        ExamSession session =
                examSessionService.startOrResumeExam(quizId, studentId);

        List<QuestionResponse> questions = new ArrayList<>();

        for (String questionId : session.getQuestionIds()) {
            Questions question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "An exam question could not be loaded"));

            // Never expose correctOptionIndex to the student.
            questions.add(new QuestionResponse(
                    question.getId(),
                    question.getQuestionText(),
                    question.getOptions(),
                    question.getPoints()
            ));
        }


        ExamSessionResponse response = new ExamSessionResponse(
                session.getId(),
                session.getQuizId(),
                session.getStatus().name(),
                session.getStartedAt(),
                session.getDeadline(),
                questions,
                session.getAnswers()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sessions/{sessionId}/submit")
    public ResponseEntity<SubmissionConfirmation> submitExam(
            @PathVariable String sessionId) {

        String studentId = requireAuthenticatedStudent();

        ExamSession session =
                examSessionService.submitExam(sessionId, studentId);

        return ResponseEntity.ok(new SubmissionConfirmation(
                session.getId(),
                session.getStatus().name(),
                session.getSubmittedAt()
        ));
    }

    public record SubmissionConfirmation(
            String sessionId,
            String status,
            LocalDateTime submittedAt
    ) {}

    public record SubmissionResponse(
            String sessionId,
            String status,
            java.math.BigDecimal score,
            java.math.BigDecimal totalMarks,
            Integer correctAnswers,
            Integer answeredQuestions,
            LocalDateTime submittedAt
    ) {
    }

    private String requireAuthenticatedStudent() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal() == null
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please log in first");
        }

        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_STUDENT"));

        if (!isStudent) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only students can access this endpoint");
        }

        // Assumes your Spring Security principal is the student's ID.
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof String studentId)
                || studentId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Student ID could not be determined from the login");
        }

        return studentId;
    }

    public record QuestionResponse(
            String id,
            String questionText,
            List<String> options,
            java.math.BigDecimal points) {
    }


    public record ExamSessionResponse(
            String sessionId,
            String quizId,
            String status,
            LocalDateTime startedAt,
            LocalDateTime deadline,
            List<QuestionResponse> questions,
            java.util.Map<String, String> savedAnswers
    ) {
    }



    @PutMapping("/sessions/{sessionId}/answers")
    public ResponseEntity<Void> saveAnswer(
            @PathVariable String sessionId,
            @RequestBody SaveAnswerRequest request) {

        String studentId = requireAuthenticatedStudent();

        if (request == null
                || request.questionId() == null
                || request.selectedOption() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Question and selected option are required");
        }

        examSessionService.saveAnswer(
                sessionId,
                studentId,
                request.questionId(),
                request.selectedOption()
        );

        return ResponseEntity.noContent().build();
    }

    public record SaveAnswerRequest(
            String questionId,
            String selectedOption) {
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<ExamSessionResponse> getExamSession(
            @PathVariable String sessionId) {

        String studentId = requireAuthenticatedStudent();

        ExamSession session =
                examSessionService.getSessionForStudent(sessionId, studentId);

        List<QuestionResponse> questions = new ArrayList<>();

        for (String questionId : session.getQuestionIds()) {
            Questions question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "An exam question could not be loaded"));

            questions.add(new QuestionResponse(
                    question.getId(),
                    question.getQuestionText(),
                    question.getOptions(),
                    question.getPoints()
            ));
        }

        ExamSessionResponse response = new ExamSessionResponse(
                session.getId(),
                session.getQuizId(),
                session.getStatus().name(),
                session.getStartedAt(),
                session.getDeadline(),
                questions,
                session.getAnswers()
        );

        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<List<ExamSessionService.StudentExamSummary>>
    getPublishedExams() {

        String studentId = requireAuthenticatedStudent();

        return ResponseEntity.ok(
                examSessionService.getPublishedExamsForStudent(studentId)
        );
    }
}
