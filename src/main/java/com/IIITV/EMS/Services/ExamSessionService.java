
package com.IIITV.EMS.Services;

import com.IIITV.EMS.Model.ExamSession;
import com.IIITV.EMS.Model.Quiz;
import com.IIITV.EMS.Model.QuizStatus;
import com.IIITV.EMS.Model.SessionStatus;
import com.IIITV.EMS.Model.Questions;
import com.IIITV.EMS.Repository.ExamSessionRepository;
import com.IIITV.EMS.Repository.QuestionRepository;
import com.IIITV.EMS.Repository.QuizRepository;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExamSessionService {

    private final ExamSessionRepository examSessionRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public ExamSessionService(
            ExamSessionRepository examSessionRepository,
            QuizRepository quizRepository,
            QuestionRepository questionRepository) {
        this.examSessionRepository = examSessionRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    public ExamSession startOrResumeExam(String quizId, String studentId) {

        if (studentId == null || studentId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Student authentication is required");
        }

        if (quizId == null || quizId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quiz ID is required");
        }

        // 1. Check whether this student already has an attempt.
        ExamSession existing = examSessionRepository
                .findByStudentIdAndQuizId(studentId, quizId)
                .orElse(null);

        if (existing != null) {
            return resumeExistingSession(existing);
        }

        // 2. Load the exam.
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exam not found"));

        if (quiz.getStatus() != QuizStatus.PUBLISHED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This exam is not available");
        }

        LocalDateTime now = LocalDateTime.now();

        if (quiz.getStartTime() == null || quiz.getEndTime() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Exam schedule is not configured");
        }

        if (now.isBefore(quiz.getStartTime())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "The exam has not started yet");
        }

        if (!now.isBefore(quiz.getEndTime())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "The exam time has ended");
        }

        // 3. Load questions in their existing database order.
        List<Questions> questions =
                questionRepository.findByQuizId(quizId);

        if (questions.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This exam has no questions");
        }

        // 4. Create one session with a fixed question order.
        ExamSession session = new ExamSession();
        session.setStudentId(studentId);
        session.setQuizId(quizId);
        session.setStatus(SessionStatus.IN_PROGRESS);
        session.setStartedAt(now);
        session.setDeadline(quiz.getEndTime());

        session.setQuestionIds(
                questions.stream()
                        .map(Questions::getId)
                        .toList());

        try {
            return examSessionRepository.save(session);
        } catch (DuplicateKeyException ex) {
            // Another request may have created the session concurrently.
            return examSessionRepository
                    .findByStudentIdAndQuizId(studentId, quizId)
                    .orElseThrow(() -> ex);
        }
    }

    private ExamSession resumeExistingSession(ExamSession session) {

        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This exam attempt has already been submitted");
        }

        if (session.getDeadline() == null
                || !LocalDateTime.now().isBefore(session.getDeadline())) {

            return finalizeSession(
                    session,
                    LocalDateTime.now(),
                    true
            );
        }

        // Return the same session: do not reset its timer or question order.
        return session;
    }


    public ExamSession saveAnswer(
            String sessionId,
            String studentId,
            String questionId,
            String selectedOption) {

        ExamSession session = examSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Exam session not found"));

        if (!session.getStudentId().equals(studentId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "This session does not belong to you");
        }

        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "This exam has already been submitted");
        }

        if (session.getDeadline() == null
                || !LocalDateTime.now().isBefore(session.getDeadline())) {

            finalizeSession(
                    session,
                    LocalDateTime.now(),
                    true
            );

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Exam time has ended"
            );
        }

        if (!session.getQuestionIds().contains(questionId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This question does not belong to the exam session");
        }

        Questions question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Question not found"));

        if (!question.getQuizId().equals(session.getQuizId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Question does not belong to this exam");
        }

        int optionIndex;

        try {
            optionIndex = Integer.parseInt(selectedOption);
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid option index");
        }

        if (optionIndex < 0 || optionIndex >= question.getOptions().size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Selected option does not exist");
        }

        // Store the selected option index as a string.
        session.getAnswers().put(questionId, String.valueOf(optionIndex));

        return examSessionRepository.save(session);
    }

    public ExamSession getSessionForStudent(
            String sessionId,
            String studentId) {

        ExamSession session = examSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exam session not found"));

        if (!session.getStudentId().equals(studentId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot access another student's exam session");
        }

        if (session.getStatus() == SessionStatus.IN_PROGRESS
                && (session.getDeadline() == null
                || !LocalDateTime.now().isBefore(session.getDeadline()))) {

            return finalizeSession(
                    session,
                    LocalDateTime.now(),
                    true
            );
        }

        return session;
    }

    public ExamSession submitExam(String sessionId, String studentId) {

        ExamSession session = examSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Exam session not found"));

        if (!session.getStudentId().equals(studentId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot submit another student's exam");
        }

        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            return session;
        }

        LocalDateTime now = LocalDateTime.now();

        boolean timeExpired = session.getDeadline() == null
                || !now.isBefore(session.getDeadline());

        return finalizeSession(session, now, timeExpired);
    }

    private ExamSession finalizeSession(
            ExamSession session,
            LocalDateTime submittedAt,
            boolean autoSubmitted
    ) {
        BigDecimal score = BigDecimal.ZERO;
        BigDecimal totalMarks = BigDecimal.ZERO;
        int correctAnswers = 0;
        int answeredQuestions = 0;

        for (String questionId : session.getQuestionIds()) {
            Questions question = questionRepository.findById(questionId)
                    .orElse(null);

            if (question == null) {
                continue;
            }

            BigDecimal points = question.getPoints() == null
                    ? BigDecimal.ZERO
                    : question.getPoints();

            totalMarks = totalMarks.add(points);

            String selectedAnswer = session.getAnswers().get(questionId);

            if (selectedAnswer == null) {
                continue;
            }

            answeredQuestions++;

            try {
                int selectedIndex = Integer.parseInt(selectedAnswer);

                if (selectedIndex == question.getCorrectOptionIndex()) {
                    correctAnswers++;
                    score = score.add(points);
                }
            } catch (NumberFormatException ignored) {
                // Invalid stored answer does not receive marks.
            }
        }

        session.setScore(score);
        session.setTotalMarks(totalMarks);
        session.setCorrectAnswers(correctAnswers);
        session.setAnsweredQuestions(answeredQuestions);
        session.setSubmittedAt(submittedAt);
        session.setStatus(
                autoSubmitted
                        ? SessionStatus.AUTO_SUBMITTED
                        : SessionStatus.SUBMITTED
        );

        return examSessionRepository.save(session);
    }

    public List<StudentExamSummary> getPublishedExamsForStudent(
            String studentId) {

        LocalDateTime now = LocalDateTime.now();

        List<Quiz> quizzes =
                quizRepository.findByStatus(QuizStatus.PUBLISHED);

        List<ExamSession> sessions =
                examSessionRepository.findByStudentId(studentId);

        Map<String, ExamSession> sessionMap = new HashMap<>();

        for (ExamSession session : sessions) {
            sessionMap.put(session.getQuizId(), session);
        }

        List<StudentExamSummary> result = new ArrayList<>();

        for (Quiz quiz : quizzes) {

            ExamSession session = sessionMap.get(quiz.getId());

            String examStatus;

            if (session != null) {
                if (session.getStatus() == SessionStatus.IN_PROGRESS) {
                    if (session.getDeadline() == null
                            || !now.isBefore(session.getDeadline())) {
                        examStatus = "AUTO_SUBMITTED";
                    } else {
                        examStatus = "IN_PROGRESS";
                    }
                } else {
                    examStatus = session.getStatus().name();
                }
            } else if (quiz.getStartTime() == null
                    || quiz.getEndTime() == null) {
                examStatus = "SCHEDULE_NOT_CONFIGURED";
            } else if (now.isBefore(quiz.getStartTime())) {
                examStatus = "UPCOMING";
            } else if (!now.isBefore(quiz.getEndTime())) {
                examStatus = "ENDED";
            } else {
                examStatus = "AVAILABLE";
            }

            result.add(new StudentExamSummary(
                    quiz.getId(),
                    quiz.getTitle(),
                    quiz.getDescription(),
                    quiz.getSubject(),
                    quiz.getDurationMinutes(),
                    quiz.getStartTime(),
                    quiz.getEndTime(),
                    quiz.getTotalMarks(),
                    examStatus,
                    session != null ? session.getId() : null
            ));
        }

        return result;
    }

    public record StudentExamSummary(
            String quizId,
            String title,
            String description,
            String subject,
            Integer durationMinutes,
            LocalDateTime startTime,
            LocalDateTime endTime,
            java.math.BigDecimal totalMarks,
            String examStatus,
            String sessionId
    ) {}

    public ExamSession saveWarningCount(String sessionId, String studentId, int totalWarnings) {
        ExamSession session = examSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Exam session not found"));

        if (!session.getStudentId().equals(studentId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "This session does not belong to you");
        }

        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Exam is not in progress");
        }

        if (totalWarnings < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Warning count cannot be negative");
        }

        session.setTotalWarnings(totalWarnings);

        boolean flagged = totalWarnings > 5;
        session.setIsFlagged(flagged);
        session.setReviewStatus(flagged ? "PENDING_REVIEW" : "NOT_REQUIRED");

        if (flagged) {
            session.setFlagReason(
                    "Total proctoring warnings reached " + totalWarnings);
        }

        session.setTotalWarnings(totalWarnings);
        return examSessionRepository.save(session);
    }
}
