package com.eep.tickets.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eep.tickets.models.Event;
import com.eep.tickets.services.EventService;

@RestController
@RequestMapping("/api/v1")
public class EventController {

	@Autowired
	private EventService eventService;

	// GET METHODS
	@GetMapping("/event")
	public ResponseEntity<List<Event>> getAll() {
		List<Event> events = eventService.getAll();
		return ResponseEntity.status(HttpStatus.OK).body(events);
	}

	@GetMapping("/event/{id}")
	public ResponseEntity<Event> getById(@PathVariable Long id) {
		Event event = eventService.getById(id);
		return ResponseEntity.status(HttpStatus.OK).body(event);
	}

	@PostMapping("/event")
	public ResponseEntity<Event> create(@RequestBody Event event) {
		Event newEvent = eventService.create(event);
		return ResponseEntity.status(HttpStatus.CREATED).body(newEvent);
	}

}
