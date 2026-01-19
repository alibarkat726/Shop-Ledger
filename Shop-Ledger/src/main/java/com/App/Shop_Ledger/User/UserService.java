package com.App.Shop_Ledger.User;

import com.App.Shop_Ledger.Dto.LoginDto;
import com.App.Shop_Ledger.Dto.SignupRequest;
import com.App.Shop_Ledger.Service.JwtService;

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
    private TenantRepo tenantRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authManager;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    // =========================
    // OWNER SIGNUP
    // =========================
    public void registerOwner(SignupRequest request) {

        // Prevent duplicate owner email
        if (userRepo.existsByUsername(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // 1️⃣ Create Tenant
        Tenant tenant = new Tenant();
        tenant.setName(request.getBusinessName());
        tenant.setPlan("FREE");
        tenant.setStatus("ACTIVE");
        tenantRepo.save(tenant);

        // 2️⃣ Create ADMIN User
        Users admin = new Users();
        admin.setUsername(request.getEmail());
        admin.setPassword(encoder.encode(request.getPassword()));
        admin.setTenantId(tenant.getId());
        admin.setRole("ADMIN");
        admin.setPermissions(List.of(
                "CREATE_RECEIPT",
                "VIEW_REPORTS",
                "MANAGE_EMPLOYEES",
                "VIEW_AI_INSIGHTS"
        ));
        admin.setStatus("ACTIVE");

        userRepo.save(admin);
    }

    // =========================
    // LOGIN (ADMIN / EMPLOYEE)
    // =========================
    public Map<String, String> login(LoginDto loginDto) {

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );

        if (!authentication.isAuthenticated()) {
            throw new RuntimeException("Invalid credentials");
        }

        Users user = userRepo.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }
}
