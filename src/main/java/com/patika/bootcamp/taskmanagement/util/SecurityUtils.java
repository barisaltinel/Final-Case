package com.patika.bootcamp.taskmanagement.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class SecurityUtils {

    /**
     * Kullanıcının belirli bir role sahip olup olmadığını kontrol eder.
     * @param role Kontrol edilecek rol (örn: "TEAM_MEMBER", "PROJECT_MANAGER")
     * @return Kullanıcı ilgili role sahipse `true`, değilse `false`
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + role));
    }
}
