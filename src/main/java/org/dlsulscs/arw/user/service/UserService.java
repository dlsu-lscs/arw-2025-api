package org.dlsulscs.arw.user.service;

import org.dlsulscs.arw.user.model.User;
import org.dlsulscs.arw.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     * Get user information by email
     * 
     * @param email
     * 
     * @return
     */
    public User getUserByEmail(String email) {
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return user;
    }
}
