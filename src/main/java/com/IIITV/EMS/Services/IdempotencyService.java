package com.IIITV.EMS.Services;

import com.IIITV.EMS.Model.IdempotencyRecord;
import com.IIITV.EMS.Repository.IdempotencyRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class IdempotencyService {

    private final IdempotencyRepository repository;

    public IdempotencyService(IdempotencyRepository repository) {
        this.repository = repository;
    }

    public String createRequestHash(String requestBody) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    requestBody.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Unable to hash request", e);
        }
    }

    public IdempotencyRecord find(String key) {
        return repository.findByIdempotencyKey(key).orElse(null);
    }

    public IdempotencyRecord save(
            String key,
            String requestHash,
            String resourceId) {

        IdempotencyRecord record = new IdempotencyRecord();
        record.setIdempotencyKey(key);
        record.setRequestHash(requestHash);
        record.setResourceId(resourceId);
        record.setCreatedAt(LocalDateTime.now());

        return repository.save(record);
    }
}