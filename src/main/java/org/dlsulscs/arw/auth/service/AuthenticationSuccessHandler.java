package org.dlsulscs.arw.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dlsulscs.arw.auth.model.RefreshToken;
import org.dlsulscs.arw.config.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties jwtProperties;
    private final String redirectUri;

    @Autowired
    public AuthenticationSuccessHandler(JwtService jwtService, RefreshTokenService refreshTokenService, JwtProperties jwtProperties, @Value("${app.oauth2.redirect-uri}") String redirectUri) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.jwtProperties = jwtProperties;
        this.redirectUri = redirectUri;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

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
