package com.App.Shop_Ledger.EmailOtp;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    public String generateAndSaveOtp(String email) {
        // Delete previous OTP if exists
        otpRepository.deleteByEmail(email);

        String otp = String.valueOf(100000 + new Random().nextInt(900000)); // 6-digit OTP

        OtpEntry otpEntry = new OtpEntry();
        otpEntry.setEmail(email);
        otpEntry.setOtp(otp);
        otpEntry.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otpEntry);

        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        Optional<OtpEntry> optional = otpRepository.findByEmail(email);

        if (optional.isEmpty()) return false;

        OtpEntry entry = optional.get();

        if (entry.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpRepository.deleteByEmail(email);
            return false;
        }

        boolean isValid = entry.getOtp().equals(otp);
        if (isValid) {
            otpRepository.deleteByEmail(email); // One-time use
        }

        return isValid;
    }
}
