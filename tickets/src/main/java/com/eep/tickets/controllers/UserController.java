package com.eep.tickets.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eep.tickets.models.User;
import com.eep.tickets.services.UserService;

@RestController
@RequestMapping("/api/v1")
public class UserController {

	@Autowired
	private UserService userService;

	// GET METHODS
	@GetMapping("user")
	public ResponseEntity<List<User>> getAll() {
		List<User> users = userService.getAll();
		return ResponseEntity.status(HttpStatus.OK).body(users);
	}

	@GetMapping("user/{id}")
	public ResponseEntity<User> getById(@PathVariable Long id) {
		User user = userService.getById(id);
		return ResponseEntity.status(HttpStatus.OK).body(user);
	}

	// POST METHODS
	@PostMapping("user")
	public ResponseEntity<User> create(@RequestBody User user) {
		User createdUser = userService.create(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
	}

	// PATCH METHODS
	@PatchMapping("/user/{id}")
	public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
		User updatedUser = userService.update(id, user);
		return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
	}

	// DELETE METHODS
	@DeleteMapping("user/{id}")
	public void delete(@PathVariable Long id) {
		userService.delete(id);
	}

}
