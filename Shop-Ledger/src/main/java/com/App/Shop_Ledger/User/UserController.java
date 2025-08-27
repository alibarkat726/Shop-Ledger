package com.App.Shop_Ledger.User;
import com.App.Shop_Ledger.Dto.LoginDto;
import com.App.Shop_Ledger.EmailOtp.EmailService;
import com.App.Shop_Ledger.EmailOtp.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService service;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<Users> register(@RequestBody Users users) {
        List<Users> users1 = userRepo.findByUsername(users.getUsername());
        if (users1 == null) {
//            String otp = otpService.generateAndSaveOtp(users.getUsername());
//            System.out.println("otp " + otp);
//            emailService.sendOtpEmail(users.getUsername(), otp);
            Users users2 = service.register(users);
            return ResponseEntity.ok(users2);
        } else {
            boolean SameRoleExists = users1.stream()
                    .anyMatch(users2 -> users2.getRole().equalsIgnoreCase(users.getRole()));
            if (SameRoleExists) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                Users newUser= service.register(users);
                return ResponseEntity.ok(newUser);
            }
        }
    }
    @PostMapping("/login")
    public Map<String,String> login(@RequestBody LoginDto loginDto){
        System.out.println("login url hitted");
        return service.verify(loginDto);
    }
}
