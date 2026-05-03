package com.cts.trialledger.apigateway.controller;

import com.cts.trialledger.apigateway.dto.RegisterDTO;
import com.cts.trialledger.apigateway.dto.UpdateUserDTO;
import com.cts.trialledger.apigateway.dto.UserDTO;
import com.cts.trialledger.apigateway.service.UserService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("register-by-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> registerByAdmin(@RequestParam String role, @Valid @RequestBody RegisterDTO dto) {
        userService.registerUser(dto, role);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @GetMapping("{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI')")
    public UserDTO getUser(@PathVariable Long userId) {
        return userService.getUserByUserId(userId);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsers(@RequestParam(required = false) String role) {
        if(role == null) return userService.getAllUsers();
        return userService.getAllUsersByRole(role);
    }

    @PutMapping("{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(@PathVariable Long userId,@Valid @RequestBody UpdateUserDTO userDTO) {
        userService.updateUser(userId, userDTO);
        return "User updated successfully!";
    }

    @PutMapping("{userId}/status")
    public String updateUserStatus(@PathVariable Long userId,@RequestParam String status) {
        userService.updateUserStatus(userId, status);
        return "User status updated successfully!";
    }


}
