package com.IIITV.EMS.Model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "activity_logs")
public class ActivityLog {

    @Id
    private String id;

    @Indexed
    private LocalDateTime occurredAt;

    private String actorId;
    private String actorName;
    private String actorRole;
    private String action;
    private String category;
    private String endpoint;
    private int statusCode;
    private String outcome;
}
