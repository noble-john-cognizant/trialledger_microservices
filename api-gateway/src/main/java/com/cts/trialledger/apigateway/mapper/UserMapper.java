package com.cts.trialledger.apigateway.mapper;

import com.cts.trialledger.apigateway.dto.UserDTO;
import com.cts.trialledger.apigateway.entity.User;

public class UserMapper {
    public static UserDTO getUserDTO(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getName(),
                user.getRole().name(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus().name(),
                user.getCreatedAt()
        );
    }
}
