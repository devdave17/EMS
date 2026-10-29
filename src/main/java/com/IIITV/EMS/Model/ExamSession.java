
package com.IIITV.EMS.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "exam_sessions")
@CompoundIndex(
        name = "unique_student_quiz",
        def = "{'studentId': 1, 'quizId': 1}",
        unique = true
)
@Data
public class ExamSession {
    @Id
    private String id;

    private String studentId;
    private String quizId;

    private SessionStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime deadline;
    private LocalDateTime submittedAt;

    private java.math.BigDecimal score =
            java.math.BigDecimal.ZERO;

    private java.math.BigDecimal totalMarks =
            java.math.BigDecimal.ZERO;

    private Integer correctAnswers = 0;

    private Integer answeredQuestions = 0;


    // Question order is fixed for this session.
    private List<String> questionIds = new ArrayList<>();

    // Key: question ID, Value: student's selected answer ID.
    private Map<String, String> answers = new LinkedHashMap<>();

    public ExamSession() {
    }

    // Proctoring summary
    private Integer totalWarnings = 0;
    private Integer headTurnWarnings = 0;
    private Integer tabSwitchWarnings = 0;
    private Integer faceDetectionWarnings = 0;
    private Integer fullscreenWarnings = 0;
    private Integer cameraWarnings = 0;

    private Boolean isFlagged = false;
    private String flagReason;
    private String reviewStatus = "NOT_REQUIRED";

    // Detailed proctoring events
    private List<ProctoringEvent> proctoringEvents = new ArrayList<>();

    @Data
    public static class ProctoringEvent {
        private String type;
        private String message;
        private LocalDateTime timestamp;

        public ProctoringEvent() {
        }

        public ProctoringEvent(
                String type,
                String message,
                LocalDateTime timestamp) {
            this.type = type;
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}
