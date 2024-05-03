package com.eep.tickets.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eep.tickets.models.Ticket;
import com.eep.tickets.services.TicketService;

@RestController
@RequestMapping("/api/v1")
public class TicketController {

	@Autowired
	private TicketService ticketService;

	// GET METHODS
	@GetMapping("ticket")
	public ResponseEntity<List<Ticket>> getAll() {
		List<Ticket> tickets = ticketService.getAll();
		return ResponseEntity.status(HttpStatus.OK).body(tickets);
	}

	@GetMapping("ticket/{id}")
	public ResponseEntity<Ticket> getById(@PathVariable Long id) {
		Ticket ticket = ticketService.getById(id);
		return ResponseEntity.status(HttpStatus.OK).body(ticket);
	}

	// POST METHODS
	@PostMapping("ticket")
	public ResponseEntity<Ticket> create(@RequestBody Ticket ticket) {
		Ticket createdTicket = ticketService.create(ticket);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
	}
}
