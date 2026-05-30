package com.cts.trialledger.client;

import com.cts.trialledger.dto.RegisterDTO;
import com.cts.trialledger.dto.UserDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "AUTH-SERVICE",
        fallback = AuthClientFallback.class)
public interface AuthClient {


    @PostMapping("/api/auth/internal/register")
    ResponseEntity<String> register(@Valid @RequestBody RegisterDTO dto);


    @GetMapping("/api/users/{userId}")
    UserDTO getUserById(@PathVariable Long userId);
}
