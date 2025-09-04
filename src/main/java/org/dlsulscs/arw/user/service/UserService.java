package org.dlsulscs.arw.user.service;

import org.dlsulscs.arw.common.exception.ResourceNotFoundException;
import org.dlsulscs.arw.user.model.User;
import org.dlsulscs.arw.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Transactional
    public User processOAuth2User(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        log.info("Processing OAuth2 user with email: {}", email);
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            log.info("User found: {}", email);
            // Potentially update user details here if they have changed
            return userOptional.get();
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(oAuth2User.getAttribute("name"));
            newUser.setDisplay_picture(oAuth2User.getAttribute("picture"));
            User savedUser = userRepository.save(newUser);
            log.info("New user saved: {}", savedUser.getEmail());
            return savedUser;
        }
    }
}
