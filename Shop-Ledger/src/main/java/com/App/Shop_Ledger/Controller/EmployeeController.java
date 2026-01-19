package com.App.Shop_Ledger.Controller;

import com.App.Shop_Ledger.Dto.AcceptInviteRequest;
import com.App.Shop_Ledger.Dto.InviteEmployeeRequest;
import com.App.Shop_Ledger.Service.EmployeeInviteService;
import com.App.Shop_Ledger.Service.InviteAcceptanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeInviteService inviteService;

    @Autowired
    private InviteAcceptanceService acceptanceService;

    // ADMIN only
    @PreAuthorize("hasAuthority('MANAGE_EMPLOYEES')")
    @PostMapping("/invite")
    public ResponseEntity<?> invite(@RequestBody InviteEmployeeRequest request) {
        inviteService.inviteEmployee(request);
        return ResponseEntity.ok("Invite sent");
    }

    // Public endpoint
    @PostMapping("/accept-invite")
    public ResponseEntity<?> accept(@RequestBody AcceptInviteRequest request) {
        acceptanceService.acceptInvite(request);
        return ResponseEntity.ok("Account activated");
    }
}

