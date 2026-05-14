package com.cts.studyandprotocol.util;


import com.cts.studyandprotocol.model.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {
    public static UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getDetails() instanceof UserDetails userDetails) {
            return userDetails;
        }
        throw new IllegalArgumentException("UserDetails not found");
    }

    public static Long getCurrentUserId(){
        return getCurrentUser().getUserId();
    }
}
