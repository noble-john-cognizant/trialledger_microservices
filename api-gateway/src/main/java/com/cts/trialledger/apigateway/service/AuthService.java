package com.cts.trialledger.apigateway.service;


import com.cts.trialledger.apigateway.dto.*;

public interface AuthService {
    LoginResponseDTO login(LoginDTO loginDTO);

    void register(RegisterDTO registerDTO);

    void forgotPassword(ForgotPasswordDTO dto);

    String forgotUsername(ForgotUsernameDTO dto);

}
