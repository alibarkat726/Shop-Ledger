package com.App.Shop_Ledger.User;
import com.App.Shop_Ledger.Dto.LoginDto;
import com.App.Shop_Ledger.Dto.SignupRequest;
import com.App.Shop_Ledger.EmailOtp.EmailService;
import com.App.Shop_Ledger.EmailOtp.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // OWNER SIGNUP (Business Registration)
    @PostMapping("/register")
    public ResponseEntity<?> registerOwner(@RequestBody SignupRequest request) {
        userService.registerOwner(request);
        return ResponseEntity.ok("Business registered successfully");
    }

    // LOGIN (Admin / Employee)
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDto loginDto) {
        Map<String, String> token = userService.login(loginDto);
        return ResponseEntity.ok(token);
    }
}

//            String otp = otpService.generateAndSaveOtp(users.getUsername());
//            System.out.println("otp " + otp);
//            emailService.sendOtpEmail(users.getUsername(), otp);