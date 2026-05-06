package com.cts.trialledger.apigateway.repository;


import com.cts.trialledger.apigateway.entity.User;
import com.cts.trialledger.apigateway.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    List<User> findAllByRole(Role role);

    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
}
