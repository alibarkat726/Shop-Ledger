package com.App.Shop_Ledger.Service;

import com.App.Shop_Ledger.Dto.AcceptInviteRequest;
import com.App.Shop_Ledger.InviteAlreadyUsedException;
import com.App.Shop_Ledger.InviteExpiredException;
import com.App.Shop_Ledger.Repository.InviteTokenRepo;
import com.App.Shop_Ledger.User.UserRepo;
import com.App.Shop_Ledger.User.Users;
import com.App.Shop_Ledger.model.InviteToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class InviteAcceptanceService {

    @Autowired
    private InviteTokenRepo inviteTokenRepo;

    @Autowired
    private UserRepo userRepo;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public void acceptInvite(AcceptInviteRequest request) {

        // 1️⃣ Find invite by token
        InviteToken invite = inviteTokenRepo.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid invite token"));

        // 2️⃣ Check if already used
        if (invite.isUsed()) {
            throw new InviteAlreadyUsedException("Invite has already been used");
        }

        // 3️⃣ Check if expired
        if (invite.getExpiresAt().before(new Date())) {
            throw new InviteExpiredException("Invite has expired");
        }

        // 4️⃣ Create new user from invite data
        Users user = new Users();
        user.setUsername(invite.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setTenantId(invite.getTenantId());
        user.setRole(invite.getRole());
        user.setPermissions(getPermissionsForRole(String.valueOf(invite.getRole())));
        user.setEnabled(true); // Enable user immediately upon acceptance
        user.setStatus("ACTIVE");

        userRepo.save(user);
        invite.setUsed(true);
        inviteTokenRepo.save(invite);
    }

    private List<String> getPermissionsForRole(String role) {
        return switch (role) {
            case "CASHIER" -> List.of("CREATE_RECEIPT");
            case "ADMIN" -> List.of("CREATE_RECEIPT", "VIEW_REPORTS", "MANAGE_EMPLOYEES", "VIEW_AI_INSIGHTS");
            case  "MANAGER" -> List.of("CREATE_RECEIPT", "VIEW_REPORTS");
            default -> throw new RuntimeException("Invalid role");
        };
    }
}


