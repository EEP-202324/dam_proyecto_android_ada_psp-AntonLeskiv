package com.eep.tickets.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


public enum Role {
	ADMIN,
	USER;

	@Override
	public String toString() {
		return this.name();
	}
}
