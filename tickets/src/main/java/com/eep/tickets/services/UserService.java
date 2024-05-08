package com.eep.tickets.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.eep.tickets.models.User;
import com.eep.tickets.repositories.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<User> getAll() {
		return userRepository.findAll();
	}

	public User getById(Long id) {
		return userRepository.findById(id).orElse(null);
	}

	public User getByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public User create(User user) {
		return userRepository.save(user);
	}

	public void delete(Long id) {
		userRepository.deleteById(id);
	}

	public User update(Long id, User updateUserDetails) {
		User user = userRepository.findById(id).orElse(null);

		// Actualizar solo los campos no nulos del objeto updateUserDetails
		if (updateUserDetails.getRole() != null) {
			user.setRole(updateUserDetails.getRole());
		}
		if (updateUserDetails.getEmail() != null) {
			user.setEmail(updateUserDetails.getEmail());
		}
		if (updateUserDetails.getPasswordHash() != null) {
			user.setPasswordHash(updateUserDetails.getPasswordHash());
		}
		if (updateUserDetails.getFirstName() != null) {
			user.setFirstName(updateUserDetails.getFirstName());
		}
		if (updateUserDetails.getLastName() != null) {
			user.setLastName(updateUserDetails.getLastName());
		}

		return userRepository.save(user);
	}

}
