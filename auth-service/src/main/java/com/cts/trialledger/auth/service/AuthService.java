package com.cts.trialledger.auth.service;


import com.cts.trialledger.auth.dto.*;

public interface AuthService {
    LoginResponseDTO login(LoginDTO loginDTO);

    void register(RegisterDTO registerDTO);

    /** Step 1: generate a 6-digit OTP for the email and log it to console. */
    void requestPasswordResetOtp(ForgotPasswordRequestOtpDTO dto);

    /** Step 2: validate OTP then update the password. */
    void forgotPassword(ForgotPasswordDTO dto);

    String forgotUsername(ForgotUsernameDTO dto);

}
