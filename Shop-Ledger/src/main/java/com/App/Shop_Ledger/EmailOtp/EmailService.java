package com.App.Shop_Ledger.EmailOtp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    // ✅ Existing OTP email (UNCHANGED)
    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP is: " + otp + "\n\nThis OTP is valid for a limited time.");
        javaMailSender.send(message);
    }

    // ✅ NEW: Employee Invite Email
    public void sendInvite(String to, String inviteLink) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("You're invited to join a business");

        message.setText(
                "Hello,\n\n" +
                        "You have been invited to join a business on Shop Ledger.\n\n" +
                        "Please click the link below to activate your account and set your password:\n\n" +
                        inviteLink + "\n\n" +
                        "⚠ This invitation link will expire in 24 hours.\n\n" +
                        "If you did not expect this invitation, you can safely ignore this email.\n\n" +
                        "Regards,\n" +
                        "Shop Ledger Team"
        );

        javaMailSender.send(message);
    }
}


