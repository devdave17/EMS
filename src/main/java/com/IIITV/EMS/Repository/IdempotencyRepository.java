package com.IIITV.EMS.Repository;

import com.IIITV.EMS.Model.IdempotencyRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface IdempotencyRepository
        extends MongoRepository<IdempotencyRecord, String> {

    Optional<IdempotencyRecord> findByIdempotencyKey(
            String idempotencyKey
    );
}