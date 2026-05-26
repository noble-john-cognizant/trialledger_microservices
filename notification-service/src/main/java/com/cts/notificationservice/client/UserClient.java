package com.cts.notificationservice.client;

import com.cts.notificationservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "api-gateway",
        fallback = UserClientFallback.class)
public interface UserClient {

    @GetMapping("/api/users/{userId}")
    UserDTO getUserById(@PathVariable Long userId);
}
