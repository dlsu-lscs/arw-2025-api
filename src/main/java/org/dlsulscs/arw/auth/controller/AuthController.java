package org.dlsulscs.arw.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.dlsulscs.arw.auth.model.RefreshToken;
import org.dlsulscs.arw.auth.service.CookieService;
import org.dlsulscs.arw.auth.service.JwtService;
import org.dlsulscs.arw.auth.service.RefreshTokenService;
import org.dlsulscs.arw.config.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final CookieService cookieService;

    @Autowired
    public AuthController(RefreshTokenService refreshTokenService, JwtService jwtService, JwtProperties jwtProperties, CookieService cookieService) {
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.cookieService = cookieService;
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refresh_token") String token, HttpServletResponse response) {
        refreshTokenService.deleteByToken(token);

        ResponseCookie accessTokenCookie = cookieService.createLogoutCookie("access_token");
        ResponseCookie refreshTokenCookie = cookieService.createLogoutCookie("refresh_token");

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ResponseEntity.ok("Logout successful");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_token") String token,
            HttpServletResponse response) {
        return refreshTokenService.findByToken(token)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtService.generateAccessToken(user);
                    ResponseCookie accessTokenCookie = cookieService.createCookie("access_token", newAccessToken, (int) (jwtProperties.accessTokenExpirationMs() / 1000));
                    response.addHeader("Set-Cookie", accessTokenCookie.toString());
                    return ResponseEntity.ok().body("Access token refreshed");
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @GetMapping("/test/authenticated")
    public ResponseEntity<String> testAuthenticated() {
        return ResponseEntity.ok("Authenticated");
    }
}
