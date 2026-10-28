package com.IIITV.EMS.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Document(collection = "question_bank")
@Data
public class QuestionBankQuestion {

    @Id
    private String id;
    private String questionBankId;
    @NotBlank(message = "Subject is required")
    @Size(max = 100, message = "Subject cannot exceed 100 characters")
    private String subject;

    @NotBlank(message = "Question text is required")
    @Size(max = 2000, message = "Question text cannot exceed 2000 characters")
    private String questionText;

    @NotEmpty(message = "At least two answer options are required")
    @Size(min = 2, max = 6, message = "A question must have between 2 and 6 options")
    private List<@NotBlank(message = "Answer options cannot be blank") @Size(max = 500, message = "Answer options cannot exceed 500 characters") String> options;

    @NotNull(message = "Correct option index is required")
    @Min(value = 0, message = "Correct option index cannot be negative")
    private Integer correctOptionIndex;

    @NotNull(message = "Question points are required")
    @Positive(message = "Question points must be greater than zero")
    private BigDecimal points;

    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
