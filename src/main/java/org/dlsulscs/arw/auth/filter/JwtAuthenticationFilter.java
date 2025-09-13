package org.dlsulscs.arw.auth.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dlsulscs.arw.auth.service.JwtService;
import org.dlsulscs.arw.common.exception.ResourceNotFoundException;
import org.dlsulscs.arw.user.model.User;
import org.dlsulscs.arw.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, @Lazy UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = getJwtFromCookie(request);

        if (jwt != null && jwtService.validateToken(jwt)) {
            Claims claims = jwtService.extractAllClaims(jwt);
            String username = claims.getSubject();
            User user;

            if (claims.containsKey("name")) {
                user = new User();
                user.setEmail(username);
                user.setName(claims.get("name", String.class));
                user.setDisplay_picture(claims.get("picture", String.class));
            } else {
                user = userRepository.findByEmail(username)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username));
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
                    new ArrayList<>());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
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