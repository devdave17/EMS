package com.IIITV.EMS.Model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "quizzes")
@Data
public class Quiz {

    @Id
    private String id;

    @NotBlank(message = "Quiz title is required")
    @Size(max = 100, message = "Quiz title cannot exceed 100 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private String createdBy;
    private QuizStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String subject;
    private Integer durationMinutes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalMarks;
}