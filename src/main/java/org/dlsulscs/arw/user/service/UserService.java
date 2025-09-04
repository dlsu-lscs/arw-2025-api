package org.dlsulscs.arw.user.service;

import org.dlsulscs.arw.common.exception.ResourceNotFoundException;
import org.dlsulscs.arw.user.model.User;
import org.dlsulscs.arw.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public User processOAuth2User(OAuth2User oAuth2User) {
        Optional<User> userOptional = userRepository.findByEmail(oAuth2User.getAttribute("email"));
        if (userOptional.isPresent()) {
            // Potentially update user details here if they have changed
            return userOptional.get();
        } else {
            User newUser = new User();
            newUser.setEmail(oAuth2User.getAttribute("email"));
            newUser.setName(oAuth2User.getAttribute("name"));
            newUser.setDisplay_picture(oAuth2User.getAttribute("picture"));
            return userRepository.save(newUser);
        }
    }
}
