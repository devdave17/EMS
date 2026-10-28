package com.IIITV.EMS.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class LoggingPasswordResetDeliveryService
        implements PasswordResetDeliveryService {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingPasswordResetDeliveryService.class);

    @Override
    public void sendResetLink(String email, String resetLink) {
        // Do not log the reset link or token.
        log.warn(
                "Password reset requested, but email delivery is not configured."
        );
    }
}