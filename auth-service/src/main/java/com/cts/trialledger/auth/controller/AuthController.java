package com.cts.trialledger.auth.controller;

import com.cts.trialledger.auth.dto.*;
import com.cts.trialledger.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto.name() + " register successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(authService.login(loginDTO));
    }

    /**
     * Step 1 of the password-reset flow — request a 6-digit OTP. The OTP is
     * printed to the auth-service console; the user reads it from there and
     * supplies it to {@code /forgot-password} along with their new password.
     */
    @PostMapping("/forgot-password/request-otp")
    public ResponseEntity<String> requestPasswordResetOtp(@Valid @RequestBody ForgotPasswordRequestOtpDTO dto) {
        authService.requestPasswordResetOtp(dto);
        return ResponseEntity.ok("OTP generated. Check the auth-service console for the 6-digit code.");
    }

    /**
     * Step 2 of the password-reset flow — submit the OTP from the console
     * together with the new password.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordDTO dto) {
        authService.forgotPassword(dto);
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/forgot-username")
    public ResponseEntity<String> forgotUsername(@Valid @RequestBody ForgotUsernameDTO dto){
        return ResponseEntity.ok(authService.forgotUsername(dto));
    }



}
