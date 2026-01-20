package com.App.Shop_Ledger.Controller;

import com.App.Shop_Ledger.Dto.InviteEmployeeRequest;
import com.App.Shop_Ledger.Service.EmployeeInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EmployeeInviteService inviteService;


    @PostMapping("/invite")
    @PreAuthorize("hasAuthority('MANAGE_EMPLOYEES')")
    public ResponseEntity<?> inviteEmployee(@RequestBody InviteEmployeeRequest request) {
        inviteService.inviteEmployee(request);
        return ResponseEntity.ok("Invite sent successfully");
    }
}
