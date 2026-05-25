package com.cts.trialledger.auth.repository;


import com.cts.trialledger.auth.entity.User;
import com.cts.trialledger.auth.model.Role;
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
