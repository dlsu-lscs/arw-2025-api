package org.dlsulscs.arw.auth.service;

import org.dlsulscs.arw.auth.model.RefreshToken;
import org.dlsulscs.arw.auth.repository.RefreshTokenRepository;
import org.dlsulscs.arw.common.exception.ResourceNotFoundException;
import org.dlsulscs.arw.config.properties.JwtProperties;
import org.dlsulscs.arw.user.model.User;
import org.dlsulscs.arw.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RefreshTokenService {
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtProperties = jwtProperties;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(String email) {
        log.info("Attempting to create refresh token for email: {}", email);
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            log.error("User not found in DB for email: {}", email);
            throw new ResourceNotFoundException("User not found with email: " + email);
        }
        User user = userOptional.get();
        log.info("User found for refresh token creation: {}", user.getEmail());

        // Delete existing token if it exists for the user
        refreshTokenRepository.deleteByUser(user);
        log.info("Deleted existing refresh token for user: {}", user.getEmail());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtProperties.refreshTokenExpirationMs()));
        refreshToken.setToken(UUID.randomUUID().toString());

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token saved for user: {}", savedToken.getUser().getEmail());
        return savedToken;
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new ResourceNotFoundException("Refresh token was expired. Please make a new sign-in request");
        }
        return token;
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }
}
