package com.App.Shop_Ledger.EmailOtp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
public class OtpEntry {
    @Id
    private Long id;
    private String email;
    private String otp;
    private LocalDateTime expiryTime;
}
