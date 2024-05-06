package com.eep.tickets.services;

import com.eep.tickets.models.User;
import com.eep.tickets.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public boolean authenticate(String email, String clientPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null && encoder.matches(clientPassword, user.getPasswordHash())) {
            return true;
        }
        return false;
    }
}
