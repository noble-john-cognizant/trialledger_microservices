package com.cts.trialledger.client;

import com.cts.trialledger.dto.RegisterDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "AUTH-SERVICE")
public interface AuthClient {

    @PostMapping("/api/auth/register")
     ResponseEntity<String> register(@Valid @RequestBody RegisterDTO dto);

    }
