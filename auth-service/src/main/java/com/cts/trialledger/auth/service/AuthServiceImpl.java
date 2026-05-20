package com.cts.trialledger.auth.service;


import com.cts.trialledger.auth.dto.*;
import com.cts.trialledger.auth.entity.User;
import com.cts.trialledger.auth.exception.UserAlreadyExistException;
import com.cts.trialledger.auth.exception.UserNotFoundException;
import com.cts.trialledger.auth.model.Role;
import com.cts.trialledger.auth.model.Status;
import com.cts.trialledger.auth.repository.UserRepository;
import com.cts.trialledger.auth.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository authRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository authRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.authRepo = authRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;

    }

    @Override
    public LoginResponseDTO login(LoginDTO loginDTO) {
        User user = authRepo.findByEmail(loginDTO.email()).orElseThrow(() -> new UserNotFoundException(loginDTO.email() + " not found."));

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());
        Authentication authenticate = authenticationManager.authenticate(token);
        if (authenticate.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        }
        //Set the user details object in security context holder
        String jwtToken = jwtUtil.generateToken(user.getUserId(), user.getName(), user.getEmail(), user.getRole().name());
        return new LoginResponseDTO(user.getName(), jwtToken, user.getRole().name(), user.getUserId(), user.getStatus().name(), user.getCreatedAt());
    }

    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        if (registerDTO.password() == null || registerDTO.password().length() < 8) {
            throw new BadCredentialsException("Password criteria not match. Password length should be greater than or equal to 8 characters.");
        }
        Optional<User> userOptional = authRepo.findByEmail(registerDTO.email());
        if (userOptional.isPresent()) {
            throw new UserAlreadyExistException(registerDTO.email() + " already exist.");
        }

        User u = new User();
        u.setEmail(registerDTO.email());
        u.setName(registerDTO.name());
        u.setPassword(passwordEncoder.encode(registerDTO.password()));
        u.setPhone(registerDTO.phone());
        u.setRole(Role.PARTICIPANT);
        u.setStatus(Status.ACTIVE);
        u.setCreatedAt(LocalDateTime.now());
        User saved = authRepo.save(u);

        // Set the authentication obj in SecurityContextHolder for getting the userId in Auditing
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(saved, null, saved.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Override
    public void forgotPassword(ForgotPasswordDTO dto) {
        User user = authRepo.findByEmail(dto.email()).orElseThrow(() -> new UserNotFoundException(dto.email() + " not found."));
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        authRepo.save(user);

    }

    @Override
    public String forgotUsername(ForgotUsernameDTO dto) {
        User user = authRepo.findByPhone(dto.phoneNumber()).orElseThrow(() -> new UserNotFoundException(dto.phoneNumber() + " not found."));
        if (passwordEncoder.matches(dto.password(), user.getPassword())) {
            return user.getEmail();
        } else throw new BadCredentialsException("Bad credentials!Password is incorrect.");
    }

}
