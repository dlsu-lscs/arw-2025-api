package org.dlsulscs.arw.auth.service;

import org.dlsulscs.arw.auth.model.RefreshToken;
import org.dlsulscs.arw.auth.repository.RefreshTokenRepository;
import org.dlsulscs.arw.common.exception.ResourceNotFoundException;
import org.dlsulscs.arw.config.properties.JwtProperties;
import org.dlsulscs.arw.user.model.User;
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
    private final JwtProperties jwtProperties;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
            JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * 
     * @param user of type User
     * @return
     */
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        log.info("Attempting to create or update refresh token for user: {}", user.getEmail());

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
        RefreshToken refreshToken;

        if (existingToken.isPresent()) {
            refreshToken = existingToken.get();
            log.info("Existing refresh token found for user: {}", user.getEmail());
        } else {
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            log.info("No existing refresh token found, creating new for user: {}", user.getEmail());
        }

        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtProperties.refreshTokenExpirationMs()));
        refreshToken.setToken(UUID.randomUUID().toString());

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token saved/updated for user: {}", savedToken.getUser().getEmail());
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
