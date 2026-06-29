package com.social_media.commonsecurity.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // neu request tu choi dang nhap (hoac request public), tra ve "System"
        if (authentication == null || !authentication.isAuthenticated()  || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of("SYSTEM");
        }
          return Optional.of(authentication.getName());
    }
}
