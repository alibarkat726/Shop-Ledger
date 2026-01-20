package com.App.Shop_Ledger.Controller;

import com.App.Shop_Ledger.Dto.AcceptInviteRequest;
import com.App.Shop_Ledger.Service.InviteAcceptanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invites")
public class InviteController {

    @Autowired
    private InviteAcceptanceService acceptanceService;
    @PostMapping("/accept")
    public ResponseEntity<?> acceptInvite(@RequestBody AcceptInviteRequest request) {
        acceptanceService.acceptInvite(request);
        return ResponseEntity.ok("Account activated successfully");
    }
}

