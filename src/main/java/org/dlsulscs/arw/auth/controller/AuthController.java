package org.dlsulscs.arw.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.dlsulscs.arw.auth.model.RefreshToken;
import org.dlsulscs.arw.auth.service.JwtService;
import org.dlsulscs.arw.auth.service.RefreshTokenService;
import org.dlsulscs.arw.config.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Autowired
    public AuthController(RefreshTokenService refreshTokenService, JwtService jwtService, JwtProperties jwtProperties) {
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refresh_token") String token, HttpServletResponse response) {
        refreshTokenService.deleteByToken(token);

        Cookie accessTokenCookie = new Cookie("access_token", null);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refresh_token", null);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok("Logout successful");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_token") String token,
            HttpServletResponse response) {
        return refreshTokenService.findByToken(token)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtService.generateAccessToken(user.getEmail());
                    Cookie cookie = new Cookie("access_token", newAccessToken);
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);
                    cookie.setMaxAge((int) (jwtProperties.accessTokenExpirationMs() / 1000));
                    response.addCookie(cookie);
                    return ResponseEntity.ok().body("Access token refreshed");
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }
}
