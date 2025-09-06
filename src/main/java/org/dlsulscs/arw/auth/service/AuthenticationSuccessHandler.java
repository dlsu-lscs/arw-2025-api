package org.dlsulscs.arw.auth.service;

import org.dlsulscs.arw.auth.model.RefreshToken;
import org.dlsulscs.arw.config.properties.JwtProperties;
import org.dlsulscs.arw.user.model.User;
import org.dlsulscs.arw.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationSuccessHandler.class);

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtProperties jwtProperties;
    private final String redirectUri;
    private final CookieService cookieService;

    @Autowired
    public AuthenticationSuccessHandler(JwtService jwtService, RefreshTokenService refreshTokenService,
            UserService userService,
            JwtProperties jwtProperties,
            @Value("${app.oauth2.redirect-uri}") String redirectUri, CookieService cookieService) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
        this.jwtProperties = jwtProperties;
        this.redirectUri = redirectUri;
        this.cookieService = cookieService;
    }

    /**
     * on successful login
     * 
     * @param
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        log.info("Authentication success for email: {}", email);

        User user = userService.processOAuth2User(oAuth2User);

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        ResponseCookie accessTokenCookie = cookieService.createCookie("access_token", accessToken,
                jwtProperties.accessTokenExpirationMs() / 1000);
        ResponseCookie refreshTokenCookie = cookieService.createCookie("refresh_token", refreshToken.getToken(),
                jwtProperties.refreshTokenExpirationMs() / 1000);

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri).build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
