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
        if (userRepo.existsByUsername(request.getEmail())) {
            throw new RuntimeException("User already exists");
        }
        String token = UUID.randomUUID().toString();
        InviteToken inviteToken = new InviteToken();
        inviteToken.setEmail(request.getEmail());
        inviteToken.setToken(token);
        inviteToken.setRole(request.getRole());
        inviteToken.setTenantId(tenantId);
        inviteToken.setExpiresAt(
                new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24) // 24h
        );
        inviteToken.setUsed(false); // Mark as unused
        inviteTokenRepo.save(inviteToken);
        String link = "https://yourapp.com/accept-invite?token=" + token;
        emailService.sendInvite(request.getEmail(), link);
    }
    private List<String> getPermissionsForRole(String role) {
        return switch (role) {
            case "CASHIER" -> List.of("CREATE_RECEIPT");
            case "MANAGER" -> List.of("CREATE_RECEIPT", "VIEW_REPORTS", "MANAGE_EMPLOYEES", "VIEW_AI_INSIGHTS","MANAGER");
            default -> throw new RuntimeException("Invalid role");
        };
    }
}