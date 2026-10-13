package com.IIITV.EMS.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "idempotency_records")
public class IdempotencyRecord {

    @Id
    private String id;

    @Indexed(unique = true)
    private String idempotencyKey;

    private String requestHash;
    private String resourceId;
    private LocalDateTime createdAt;
}