package com.etf.crm.filters;

import com.etf.crm.entities.User;
import com.etf.crm.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Optional;

@Component
public class SetCurrentUserFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/auth/login";

    @Autowired
    private UserService userService;

    private static final ThreadLocal<User> currentUserThreadLocal = new ThreadLocal<>();

    public static User getCurrentUser() {
        return currentUserThreadLocal.get();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().contains(LOGIN_PATH)) {
            filterChain.doFilter(request, response);
            return;
        }
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        String usernameHeader = wrappedRequest.getHeader("X-Username");
        if (usernameHeader != null && !usernameHeader.isEmpty()) {
            currentUserThreadLocal.set(this.userService.getByUsernameAndDeletedFalse(usernameHeader));
        }
        filterChain.doFilter(wrappedRequest, response);
        currentUserThreadLocal.remove();
    }
}
