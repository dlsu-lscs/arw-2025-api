package org.dlsulscs.arw.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dlsulscs.arw.auth.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RedirectAuthenticatedUserFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final String frontendRedirectUri;

    public RedirectAuthenticatedUserFilter(JwtService jwtService, @Value("${app.oauth2.redirect-uri}") String frontendRedirectUri) {
        this.jwtService = jwtService;
        this.frontendRedirectUri = frontendRedirectUri;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Only apply this filter to the Google login endpoint
        if (request.getRequestURI().equals("/oauth2/authorization/google")) {
            String jwt = getJwtFromCookie(request);

            // If the user has a valid JWT and is already authenticated, redirect them to the frontend
            if (jwt != null && jwtService.validateToken(jwt) && SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                response.sendRedirect(frontendRedirectUri);
                return; // Stop further processing
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
