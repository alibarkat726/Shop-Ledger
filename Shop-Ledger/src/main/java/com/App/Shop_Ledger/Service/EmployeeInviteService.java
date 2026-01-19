package com.App.Shop_Ledger.Service;

import com.App.Shop_Ledger.Dto.InviteEmployeeRequest;
import com.App.Shop_Ledger.EmailOtp.EmailService;
import com.App.Shop_Ledger.Repository.InviteTokenRepo;
import com.App.Shop_Ledger.User.TenantContext;
import com.App.Shop_Ledger.User.UserRepo;
import com.App.Shop_Ledger.User.Users;
import com.App.Shop_Ledger.model.InviteToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeInviteService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private InviteTokenRepo inviteTokenRepo;

    @Autowired
    private EmailService emailService;

    public void inviteEmployee(InviteEmployeeRequest request) {

        String tenantId = TenantContext.getTenantId();

        // Prevent duplicate email
        if (userRepo.existsByUsername(request.getEmail())) {
            throw new RuntimeException("User already exists");
        }

        // 1️⃣ Create PENDING user
        Users user = new Users();
        user.setUsername(request.getEmail());
        user.setTenantId(tenantId);
        user.setRole(request.getRole());
        user.setPermissions(getPermissionsForRole(request.getRole()));
        user.setStatus("PENDING");

        userRepo.save(user);

        // 2️⃣ Generate invite token
        String token = UUID.randomUUID().toString();

        InviteToken inviteToken = new InviteToken();
        inviteToken.setUserId(user.getId());
        inviteToken.setToken(token);
        inviteToken.setExpiresAt(
                new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24) // 24h
        );

        inviteTokenRepo.save(inviteToken);

        // 3️⃣ Send email
        String link = "https://yourapp.com/accept-invite?token=" + token;
        emailService.sendInvite(request.getEmail(), link);
    }

    private List<String> getPermissionsForRole(String role) {
        return switch (role) {
            case "CASHIER" -> List.of("CREATE_RECEIPT");
            case "MANAGER" -> List.of("CREATE_RECEIPT", "VIEW_REPORTS");
            default -> throw new RuntimeException("Invalid role");
        };
    }
}

