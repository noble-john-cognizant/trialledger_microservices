package com.cts.notificationservice.client;

import com.cts.notificationservice.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {
    @Override
    public UserDTO getUserById(Long userId) {
        return null;
    }
}
