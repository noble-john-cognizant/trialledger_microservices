package com.cts.trialledger.auth.service;


import com.cts.trialledger.auth.dto.*;

public interface AuthService {
    LoginResponseDTO login(LoginDTO loginDTO);

    void register(RegisterDTO registerDTO);

    void forgotPassword(ForgotPasswordDTO dto);

    String forgotUsername(ForgotUsernameDTO dto);

}
