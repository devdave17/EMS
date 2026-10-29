
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
        List<Questions> questions = questionRepository.findByQuizId(quizId);

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
                    true);
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
                    true);

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Exam time has ended");
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
                    true);
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
            boolean autoSubmitted) {
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
                        : SessionStatus.SUBMITTED);

        return examSessionRepository.save(session);
    }

    public List<StudentExamSummary> getPublishedExamsForStudent(
            String studentId) {

        LocalDateTime now = LocalDateTime.now();

        List<Quiz> quizzes = quizRepository.findByStatus(QuizStatus.PUBLISHED);

        List<ExamSession> sessions = examSessionRepository.findByStudentId(studentId);

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
                    session != null ? session.getId() : null,
                    session != null && quiz.isResultsPublished()));
        }

        return result;
    }

    public List<TeacherExamResults> getTeacherResults(String teacherId) {
        List<TeacherExamResults> results = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Quiz quiz : quizRepository.findByCreatedBy(teacherId)) {
            List<TeacherSubmission> submissions = examSessionRepository
                    .findByQuizId(quiz.getId())
                    .stream()
                    .map(session -> new TeacherSubmission(
                            session.getId(),
                            session.getStudentId(),
                            session.getStatus().name(),
                            session.getScore(),
                            session.getTotalMarks(),
                            session.getCorrectAnswers(),
                            session.getAnsweredQuestions(),
                            session.getSubmittedAt()))
                    .toList();

            boolean examEnded = quiz.getEndTime() != null
                    && !now.isBefore(quiz.getEndTime());

            results.add(new TeacherExamResults(
                    quiz.getId(),
                    quiz.getTitle(),
                    quiz.getSubject(),
                    quiz.getEndTime(),
                    quiz.isResultsPublished(),
                    examEnded && !quiz.isResultsPublished(),
                    submissions));
        }

        return results;
    }

    public Quiz publishResults(String quizId, String teacherId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Exam not found"));

        if (teacherId == null || !teacherId.equals(quiz.getCreatedBy())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only the teacher who created this exam can publish its results");
        }

        LocalDateTime now = LocalDateTime.now();
        if (quiz.getEndTime() == null || now.isBefore(quiz.getEndTime())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Results can be published after the scheduled exam end time");
        }

        List<Questions> questions = questionRepository.findByQuizId(quizId);
        if (questions.isEmpty() || questions.stream().anyMatch(question -> question.getCorrectOptionIndex() == null
                || question.getOptions() == null
                || question.getCorrectOptionIndex() < 0
                || question.getCorrectOptionIndex() >= question.getOptions().size())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Every exam question must have a valid correct answer before publishing");
        }

        for (ExamSession session : examSessionRepository.findByQuizId(quizId)) {
            if (session.getStatus() == SessionStatus.IN_PROGRESS) {
                finalizeSession(session, now, true);
            }
        }

        if (!quiz.isResultsPublished()) {
            quiz.setResultsPublished(true);
            quiz.setResultsPublishedAt(now);
        }

        return quizRepository.save(quiz);
    }

    public StudentExamResult getStudentResult(String quizId, String studentId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Exam not found"));

        if (!quiz.isResultsPublished()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "The teacher has not published this exam's results yet");
        }

        ExamSession session = examSessionRepository
                .findByStudentIdAndQuizId(studentId, quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No submission found for this exam"));

        if (session.getStatus() == SessionStatus.IN_PROGRESS) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Submit the exam before viewing its result");
        }

        List<QuestionResult> questionResults = new ArrayList<>();
        for (String questionId : session.getQuestionIds()) {
            Questions question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "An exam question could not be loaded"));

            Integer selectedIndex = null;
            String savedAnswer = session.getAnswers().get(questionId);
            if (savedAnswer != null) {
                try {
                    selectedIndex = Integer.valueOf(savedAnswer);
                } catch (NumberFormatException ignored) {
                    selectedIndex = null;
                }
            }

            int correctIndex = question.getCorrectOptionIndex();
            boolean correct = selectedIndex != null && selectedIndex == correctIndex;
            BigDecimal points = question.getPoints() == null
                    ? BigDecimal.ZERO
                    : question.getPoints();

            questionResults.add(new QuestionResult(
                    question.getId(),
                    question.getQuestionText(),
                    question.getOptions(),
                    selectedIndex,
                    selectedIndex != null && selectedIndex >= 0
                            && selectedIndex < question.getOptions().size()
                            ? question.getOptions().get(selectedIndex)
                            : null,
                    correctIndex,
                    question.getOptions().get(correctIndex),
                    correct,
                    points,
                    correct ? points : BigDecimal.ZERO));
        }

        return new StudentExamResult(
                session.getId(),
                quiz.getId(),
                quiz.getTitle(),
                session.getStatus().name(),
                session.getScore(),
                session.getTotalMarks(),
                session.getCorrectAnswers(),
                session.getAnsweredQuestions(),
                session.getSubmittedAt(),
                questionResults);
    }

    public List<StudentResultSummary> getStudentResults(String studentId) {
        List<StudentResultSummary> results = new ArrayList<>();

        for (ExamSession session : examSessionRepository.findByStudentId(studentId)) {
            if (session.getStatus() == SessionStatus.IN_PROGRESS) {
                continue;
            }

            Quiz quiz = quizRepository.findById(session.getQuizId()).orElse(null);
            if (quiz == null) {
                continue;
            }

            results.add(new StudentResultSummary(
                    quiz.getId(),
                    quiz.getTitle(),
                    session.getStatus().name(),
                    quiz.isResultsPublished(),
                    quiz.isResultsPublished() ? session.getScore() : null,
                    quiz.isResultsPublished() ? session.getTotalMarks() : null,
                    session.getSubmittedAt()));
        }

        return results;
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
            String sessionId,
            boolean resultsPublished) {
    }

    public record TeacherExamResults(
            String quizId,
            String title,
            String subject,
            LocalDateTime endTime,
            boolean resultsPublished,
            boolean canPublish,
            List<TeacherSubmission> submissions) {
    }

    public record TeacherSubmission(
            String sessionId,
            String studentId,
            String status,
            BigDecimal score,
            BigDecimal totalMarks,
            Integer correctAnswers,
            Integer answeredQuestions,
            LocalDateTime submittedAt) {
    }

    public record StudentExamResult(
            String sessionId,
            String quizId,
            String title,
            String status,
            BigDecimal score,
            BigDecimal totalMarks,
            Integer correctAnswers,
            Integer answeredQuestions,
            LocalDateTime submittedAt,
            List<QuestionResult> questions) {
    }

    public record StudentResultSummary(
            String quizId,
            String title,
            String status,
            boolean resultsPublished,
            BigDecimal score,
            BigDecimal totalMarks,
            LocalDateTime submittedAt) {
    }

    public record QuestionResult(
            String questionId,
            String questionText,
            List<String> options,
            Integer selectedOptionIndex,
            String selectedAnswer,
            Integer correctOptionIndex,
            String correctAnswer,
            boolean correct,
            BigDecimal points,
            BigDecimal pointsEarned) {
    }

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
