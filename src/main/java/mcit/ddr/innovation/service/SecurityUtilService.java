package mcit.ddr.innovation.service;

import mcit.ddr.innovation.entity.CustomUserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtilService {

    public Long getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetail) {
            CustomUserDetail userDetails = (CustomUserDetail) authentication.getPrincipal();
            return userDetails.getId();
        }

        throw new RuntimeException("No authenticated user found"); // Optional: more helpful than returning null
    }
}
