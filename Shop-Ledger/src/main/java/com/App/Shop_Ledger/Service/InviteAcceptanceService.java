package com.App.Shop_Ledger.Service;

import com.App.Shop_Ledger.Dto.AcceptInviteRequest;
import com.App.Shop_Ledger.Repository.InviteTokenRepo;
import com.App.Shop_Ledger.User.UserRepo;
import com.App.Shop_Ledger.User.Users;
import com.App.Shop_Ledger.model.InviteToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class InviteAcceptanceService {

    @Autowired
    private InviteTokenRepo inviteTokenRepo;

    @Autowired
    private UserRepo userRepo;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public void acceptInvite(AcceptInviteRequest request) {

        InviteToken invite = inviteTokenRepo.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (invite.getExpiresAt().before(new Date())) {
            throw new RuntimeException("Invite expired");
        }

        Users user = userRepo.findById(invite.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(encoder.encode(request.getPassword()));
        user.setStatus("ACTIVE");

        userRepo.save(user);

        // Cleanup token
        inviteTokenRepo.deleteByUserId(user.getId());
    }
}

