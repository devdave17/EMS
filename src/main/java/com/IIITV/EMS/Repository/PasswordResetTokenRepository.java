package com.IIITV.EMS.Repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ems.model.PasswordResetToken;

public interface PasswordResetTokenRepository
        extends MongoRepository<PasswordResetToken, String> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    void deleteByUserId(String userId);
}