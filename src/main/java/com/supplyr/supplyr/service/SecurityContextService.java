package com.supplyr.supplyr.service;

import com.supplyr.supplyr.exception.NotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextService {

    /**
     * Delete Organisational Unit from database
     *
     * @return Name of currently authenticated user in the context
     */
    public String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
