package com.App.Shop_Ledger.User;

import com.App.Shop_Ledger.Dto.LoginDto;
import com.App.Shop_Ledger.Service.JwtService;
import com.App.Shop_Ledger.WebSocket.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JwtService jwtService;

    @Autowired
    AuthenticationManager authManager;


    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Users register(Users users){
        try {
        users.setPassword(encoder.encode(users.getPassword()));
        if (users.getRole() == null || users.getRole().isEmpty()){
            users.setRole("USER");
        }
    userRepo.save(users);
    return users;
    }catch (Exception e){
        throw new RuntimeException("unable to register");}
    }

    public Map<String, String> verify(LoginDto loginDto) {
        Authentication authentication = authManager.
                authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(),loginDto.getPassword()));
        List<Users> login = userRepo.findByUsername(loginDto.getUsername());
        boolean SameRoleExists = login.stream()
                .anyMatch(users2 -> users2.getRole().equalsIgnoreCase(loginDto.getRole()));
        if (authentication.isAuthenticated() && SameRoleExists){
            String token = jwtService.generateToken(loginDto.getUsername(),loginDto.getPassword(), loginDto.getRole());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return response;
        }else {
         throw new RuntimeException("enter a valid role");
        }
    }

}
