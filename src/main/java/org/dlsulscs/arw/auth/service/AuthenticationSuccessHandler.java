package org.dlsulscs.arw.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dlsulscs.arw.auth.model.RefreshToken;
import org.dlsulscs.arw.common.exception.ResourceNotFoundException;
import org.dlsulscs.arw.config.properties.JwtProperties;
import org.dlsulscs.arw.user.model.User;
import org.dlsulscs.arw.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationSuccessHandler.class);

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService; // Inject UserService
    private final JwtProperties jwtProperties;
    private final String redirectUri;

    @Autowired
    public AuthenticationSuccessHandler(JwtService jwtService, RefreshTokenService refreshTokenService,
            UserService userService, JwtProperties jwtProperties,
            @Value("${app.oauth2.redirect-uri}") String redirectUri) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService; // Assign injected UserService
        this.jwtProperties = jwtProperties;
        this.redirectUri = redirectUri;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        log.info("Authentication success for email: {}", email);

        // Explicitly check if user exists before creating refresh token
        try {
            User user = userService.getUserByEmail(email);
            log.info("User found in AuthenticationSuccessHandler: {}", user.getEmail());
        } catch (ResourceNotFoundException e) {
            log.error("User not found in AuthenticationSuccessHandler after OAuth2 process: {}", email);
            // This indicates a serious issue if user was supposed to be provisioned
            throw e; // Re-throw to propagate the error
        }

        String accessToken = jwtService.generateAccessToken(email);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(email);

        addCookie(response, "access_token", accessToken, jwtProperties.accessTokenExpirationMs() / 1000);
        addCookie(response, "refresh_token", refreshToken.getToken(), jwtProperties.refreshTokenExpirationMs() / 1000);

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri).build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void addCookie(HttpServletResponse response, String name, String value, long maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) maxAge);
        // cookie.setSecure(true); // Enable this in production when using HTTPS
        response.addCookie(cookie);
    }
}
