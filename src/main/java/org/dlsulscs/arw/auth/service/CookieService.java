package org.dlsulscs.arw.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CookieService {

    private final String cookieDomain;
    private final String sameSite;
    private final boolean secure;

    public CookieService(
            @Value("${app.cookie.domain:}") String cookieDomain,
            @Value("${app.cookie.samesite:Lax}") String sameSite,
            @Value("${app.cookie.secure:true}") boolean secure) {
        this.cookieDomain = cookieDomain;
        this.sameSite = sameSite;
        this.secure = secure;
    }

    public ResponseCookie createCookie(String name, String value, long maxAgeSeconds) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(this.secure)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite(this.sameSite);

        if (StringUtils.hasText(this.cookieDomain)) {
            builder.domain(this.cookieDomain);
        }

        return builder.build();
    }

    public ResponseCookie createLogoutCookie(String name) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(this.secure)
                .path("/")
                .maxAge(0)
                .sameSite(this.sameSite);

        if (StringUtils.hasText(this.cookieDomain)) {
            builder.domain(this.cookieDomain);
        }

        return builder.build();
    }
}
