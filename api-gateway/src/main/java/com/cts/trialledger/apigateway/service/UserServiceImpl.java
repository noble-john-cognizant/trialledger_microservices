package com.cts.trialledger.apigateway.service;


import com.cts.trialledger.apigateway.dto.RegisterDTO;
import com.cts.trialledger.apigateway.dto.UpdateUserDTO;
import com.cts.trialledger.apigateway.dto.UserDTO;
import com.cts.trialledger.apigateway.entity.User;
import com.cts.trialledger.apigateway.exception.UserAlreadyExistException;
import com.cts.trialledger.apigateway.exception.UserNotFoundException;
import com.cts.trialledger.apigateway.mapper.UserMapper;
import com.cts.trialledger.apigateway.model.Role;
import com.cts.trialledger.apigateway.model.Status;
import com.cts.trialledger.apigateway.repository.UserRepository;
import com.cts.trialledger.apigateway.util.AuthValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @Override
    public UserDTO getUserByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User ID: " + userId + " not found!"));
        return UserMapper.getUserDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<UserDTO> list = userRepository.findAll().stream().map(UserMapper::getUserDTO).toList();
        return list;
    }

    @Override
    public List<UserDTO> getAllUsersByRole(String role) {
        Role r = AuthValidator.validateRole(role);
        List<UserDTO> list = userRepository.findAllByRole(r).stream().map(UserMapper::getUserDTO).toList();
        return list;
    }


    @Transactional
    @Override
    public void updateUser(Long userId, UpdateUserDTO userDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User ID: " + userId + " not found!"));
        if (userDTO.name() != null && !userDTO.name().isBlank())
            user.setName(userDTO.name());
        if (userDTO.email() != null && !userDTO.email().isBlank()) {
            if (userRepository.existsByEmail(userDTO.email()))
                throw new UserAlreadyExistException(userDTO.email() + " already exists!");
            else user.setEmail(userDTO.email());
        }
        if (userDTO.phone() != null && !userDTO.phone().isBlank()) {
            if (userRepository.existsByPhone(userDTO.phone()))
                throw new UserAlreadyExistException(userDTO.phone() + " already exists!");
            else user.setPhone(userDTO.phone());
        }
    }

    @Transactional
    @Override
    public void updateUserStatus(Long userId, String status) {
        Status s = AuthValidator.validateStatus(status);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User ID: " + userId + " not found!"));
        user.setStatus(s);
    }


    @Override
    public void registerUser(RegisterDTO dto, String role) {
        Role r = AuthValidator.validateRole(role);
        Optional<User> userOptional = userRepository.findByEmail(dto.email());
        if (userOptional.isPresent()) throw new UserAlreadyExistException(dto.email() + " already exist.");
        User user = new User();
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setEmail(dto.email());
        user.setName(dto.name());
        user.setPhone(dto.phone());
        user.setRole(r);
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
