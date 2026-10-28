
package com.IIITV.EMS.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateExamRequest {

    @NotBlank(message = "Exam title is required")
    @Size(max = 100)
    private String title;

    @NotBlank(message = "Subject is required")
    private String subject;

    @Size(max = 1000)
    private String description;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer durationMinutes;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotEmpty(message = "Select at least one question bank")
    private List<@NotBlank String> questionBankIds;
}
