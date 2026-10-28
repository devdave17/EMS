package com.IIITV.EMS.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "question_banks")
public class QuestionBank {

    @Id
    private String id;

    private String name;
    private String subject;
    private String description;

    // MongoDB User ID of the teacher who owns this bank
    private String createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
