package com.IIITV.EMS.Services;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ems.model.PasswordResetToken;
import com.IIITV.EMS.Model.User;
import com.IIITV.EMS.Repository.PasswordResetTokenRepository;
import com.IIITV.EMS.Repository.UserRepository;

@Slf4j
@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetDeliveryService deliveryService;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${ems.frontend.base-url:http://localhost:8080}")
    private String baseUrl;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            PasswordResetDeliveryService deliveryService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.deliveryService = deliveryService;
    }



    public void requestReset(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }

        String normalizedEmail = email.trim().toLowerCase();

        userRepository.findByEmail(normalizedEmail)
                .filter(user -> user.getId() != null)
                .ifPresent(user -> {

                    tokenRepository.deleteByUserId(user.getId());

                    byte[] randomBytes = new byte[32];
                    secureRandom.nextBytes(randomBytes);

                    String rawToken = Base64.getUrlEncoder()
                            .withoutPadding()
                            .encodeToString(randomBytes);

                    PasswordResetToken resetToken =
                            new PasswordResetToken();

                    resetToken.setUserId(user.getId());
                    resetToken.setTokenHash(sha256(rawToken));
                    resetToken.setCreatedAt(Instant.now());
                    resetToken.setExpiresAt(
                            Instant.now().plusSeconds(900)
                    );

                    tokenRepository.save(resetToken);

                    String resetLink = baseUrl
                            + "/auth/reset-password?token="
                            + rawToken;

                    deliveryService.sendResetLink(
                            user.getEmail(), resetLink
                    );
                });
    }



    public void resetPassword(String rawToken, String newPassword) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Reset link is invalid or has expired."
            );
        }

        PasswordResetToken resetToken = tokenRepository
                .findByTokenHash(sha256(rawToken))
                .filter(token -> token.getUsedAt() == null)
                .filter(token -> token.getExpiresAt() != null)
                .filter(token -> token.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Reset link is invalid or has expired."
                ));

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Reset link is invalid or has expired."
                ));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsedAt(Instant.now());
        tokenRepository.save(resetToken);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    value.getBytes(StandardCharsets.UTF_8)
            );
            return HexFormat.of().formatHex(hash);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to process reset token.", exception
            );
        }
    }
}