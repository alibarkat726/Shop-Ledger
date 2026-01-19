package com.App.Shop_Ledger.Service;
import com.App.Shop_Ledger.User.UserRepo;
import com.App.Shop_Ledger.User.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    public MyUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users usersList = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


        // Use the username and password from one of the records (assuming they're consistent)

        // Merge roles from all user entries
        Set<String> roleSet = new HashSet<>();

            if (usersList.getRole() != null) {
                Arrays.stream(usersList.getRole().split(","))
                        .map(String::trim)
                        .forEach(roleSet::add);
            }


        List<SimpleGrantedAuthority> authorities = roleSet.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                usersList.getUsername(),
                usersList.getPassword(),
                authorities);
    }
    }

//        List <Users> users = userRepo.findByUsername(username);
//        if (users == null) {
//            throw new RuntimeException("user not found");
//        } else {
//            // Split roles by comma, trim each role, and add "ROLE_" prefix
//            List<SimpleGrantedAuthority> authorities = Arrays.stream(users.getRole().split(","))
//                    .map(String::trim)
//                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
//                    .collect(Collectors.toList());
//
//            return new org.springframework.security.core.userdetails.User(users.getUsername(), users.getPassword(), authorities);
//        }
//    }

