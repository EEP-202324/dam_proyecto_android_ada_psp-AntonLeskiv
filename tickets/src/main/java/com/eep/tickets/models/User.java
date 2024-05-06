package com.eep.tickets.models;

import com.eep.tickets.controllers.AuthenticationController;
import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(unique = true)
	private String email;

	private String passwordHash;
	private String firstName;
	private String lastName;

	public User() {
	}

	public User(String email, String passwordHash, String firstName, String lastName, Role role) {
		this.email = email;
		this.passwordHash = passwordHash;
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "User{" + "id=" + id + ", role=" + role + ", email='" + email + '\'' + ", passwordHash='" + passwordHash
				+ '\'' + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + '}';
	}

}
