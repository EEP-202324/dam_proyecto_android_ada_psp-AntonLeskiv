package com.eep.tickets.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eep.tickets.models.User;
import com.eep.tickets.repositories.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

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

	public User create(User user) {
		return userRepository.save(user);
	}

	public void delete(Long id) {
		userRepository.deleteById(id);
	}

	public User update(Long id, User userDetails) {
		User user = userRepository.findById(id).orElse(null);

		// Actualizar solo los campos no nulos del objeto userDetails
		if (userDetails.getRole() != null) {
			user.setRole(userDetails.getRole());
		}
		if (userDetails.getEmail() != null) {
			user.setEmail(userDetails.getEmail());
		}
		if (userDetails.getPasswordHash() != null) {
			user.setPasswordHash(userDetails.getPasswordHash());
		}
		if (userDetails.getFirstName() != null) {
			user.setFirstName(userDetails.getFirstName());
		}
		if (userDetails.getLastName() != null) {
			user.setLastName(userDetails.getLastName());
		}

		return userRepository.save(user);
	}

}
