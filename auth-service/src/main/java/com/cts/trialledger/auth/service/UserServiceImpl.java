package com.cts.trialledger.auth.service;



import com.cts.trialledger.auth.dto.RegisterDTO;
import com.cts.trialledger.auth.dto.UpdateUserDTO;
import com.cts.trialledger.auth.dto.UserDTO;
import com.cts.trialledger.auth.entity.User;
import com.cts.trialledger.auth.exception.RoleNotAllowed;
import com.cts.trialledger.auth.exception.UserAlreadyExistException;
import com.cts.trialledger.auth.exception.UserNotFoundException;
import com.cts.trialledger.auth.mapper.UserMapper;
import com.cts.trialledger.auth.model.Role;
import com.cts.trialledger.auth.model.Status;
import com.cts.trialledger.auth.repository.UserRepository;
import com.cts.trialledger.auth.util.AuthValidator;
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

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        if (userDTO.name() != null && !userDTO.name().isBlank() && !userDTO.name().equals(user.getName())) {}
            user.setName(userDTO.name());
        if (userDTO.email() != null && !userDTO.email().isBlank() && !userDTO.email().equals(user.getEmail()) ) {
            if (userRepository.existsByEmail(userDTO.email()))
                throw new UserAlreadyExistException(userDTO.email() + " already exists!");
            else user.setEmail(userDTO.email());
        }
        if (userDTO.phone() != null && !userDTO.phone().isBlank() &&  !userDTO.phone().equals(user.getPhone()) ) {
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
        if (r.equals(Role.PARTICIPANT)) throw new
                RoleNotAllowed("Role: Participant can't be registered here. Participant automatically registered when they enrolled in study.");
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
