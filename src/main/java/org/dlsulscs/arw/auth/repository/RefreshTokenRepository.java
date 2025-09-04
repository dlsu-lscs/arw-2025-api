package org.dlsulscs.arw.auth.repository;

import org.dlsulscs.arw.auth.model.RefreshToken;
import org.dlsulscs.arw.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}
