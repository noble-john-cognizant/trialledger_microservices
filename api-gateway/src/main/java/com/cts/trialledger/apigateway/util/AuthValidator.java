package com.cts.trialledger.apigateway.util;

import com.cts.trialledger.apigateway.entity.User;
import com.cts.trialledger.apigateway.exception.InvalidEnumValueException;
import com.cts.trialledger.apigateway.model.Role;
import com.cts.trialledger.apigateway.model.Status;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthValidator {

    public static Role validateRole(String role) {
        try {
            return Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValueException(e.getMessage());
        }
    }

    public static Status validateStatus(String status) {
        try {
            return Status.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValueException(e.getMessage());
        }
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User userDetails)
            return userDetails;

        return null;
    }

    public static Long getCurrentUserId() {
        User currentUser = getCurrentUser();
        return currentUser == null ? null : currentUser.getUserId();
    }
}
