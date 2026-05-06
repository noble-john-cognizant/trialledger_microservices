package com.cts.trialledger.apigateway.service;

import com.cts.trialledger.apigateway.dto.RegisterDTO;
import com.cts.trialledger.apigateway.dto.UpdateUserDTO;
import com.cts.trialledger.apigateway.dto.UserDTO;

import java.util.List;


public interface UserService {

    UserDTO getUserByUserId(Long userId);

    List<UserDTO> getAllUsers();

    List<UserDTO> getAllUsersByRole(String role);

    void updateUser(Long userId, UpdateUserDTO userDTO);

    void updateUserStatus(Long userId, String status);

    void registerUser(RegisterDTO dto, String role);
}
