package com.IIITV.EMS.Controller;

import com.IIITV.EMS.Model.Quiz;
import com.IIITV.EMS.Model.QuizStatus;
import com.IIITV.EMS.Repository.QuizRepository;
import com.IIITV.EMS.Services.ExamSessionService;
import com.IIITV.EMS.Services.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminManagementController {
    private final QuizRepository quizRepository;
    private final QuizService quizService;
    private final ExamSessionService examSessionService;

    public AdminManagementController(
            QuizRepository quizRepository,
            QuizService quizService,
            ExamSessionService examSessionService) {
        this.quizRepository = quizRepository;
        this.quizService = quizService;
        this.examSessionService = examSessionService;
    }

    @GetMapping("/exams")
    public List<Quiz> getExams() {
        requireAdmin();
        return quizRepository.findAll();
    }

    @PutMapping("/exams/{id}/status")
    public Quiz updateExamStatus(@PathVariable String id, @RequestBody QuizStatus status) {
        requireAdmin();
        return quizService.updateQuizStatus(id, status);
    }

    @DeleteMapping("/exams/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable String id) {
        requireAdmin();
        quizRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/results")
    public List<ExamSessionService.TeacherExamResults> getResults() {
        requireAdmin();
        Map<String, ExamSessionService.TeacherExamResults> results = new LinkedHashMap<>();
        for (Quiz quiz : quizRepository.findAll()) {
            for (ExamSessionService.TeacherExamResults result
                    : examSessionService.getTeacherResults(quiz.getCreatedBy())) {
                results.put(result.quizId(), result);
            }
        }
        return new ArrayList<>(results.values());
    }

    @PostMapping("/results/{quizId}/publish")
    public Quiz publishResults(@PathVariable String quizId) {
        requireAdmin();
        return examSessionService.publishResultsAsAdmin(quizId);
    }

    private void requireAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getAuthorities().stream()
                .noneMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
    }
}
