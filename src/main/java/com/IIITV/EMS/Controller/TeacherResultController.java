package com.IIITV.EMS.Controller;

import com.IIITV.EMS.Model.Quiz;
import com.IIITV.EMS.Services.ExamSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/results")
public class TeacherResultController {

    private final ExamSessionService examSessionService;

    public TeacherResultController(ExamSessionService examSessionService) {
        this.examSessionService = examSessionService;
    }

    @GetMapping
    public ResponseEntity<List<ExamSessionService.TeacherExamResults>> getResults() {
        return ResponseEntity.ok(
                examSessionService.getTeacherResults(requireAuthenticatedTeacher()));
    }

    @PostMapping("/{quizId}/publish")
    public ResponseEntity<Quiz> publishResults(@PathVariable String quizId) {
        Quiz quiz = examSessionService.publishResults(
                quizId, requireAuthenticatedTeacher());
        return ResponseEntity.ok(quiz);
    }

    private String requireAuthenticatedTeacher() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof String teacherId)
                || "anonymousUser".equals(teacherId)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authenticated teacher is required");
        }

        boolean isTeacher = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_TEACHER".equals(authority.getAuthority()));

        if (!isTeacher) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only teachers can publish exam results");
        }

        return teacherId;
    }
}
