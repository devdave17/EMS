package com.IIITV.EMS.Services;

public interface PasswordResetDeliveryService {

    void sendResetLink(String email, String resetLink);
}