package com.eep.tickets.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
	public ResponseEntity<Page<Event>> getAllEvents(
			@PageableDefault(size = 10, sort = "date") Pageable pageable) {
		Page<Event> events = eventService.getAll(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(events);
	}

	@GetMapping("/event/{id}")
	public ResponseEntity<Event> getById(@PathVariable Long id) {
		Event event = eventService.getById(id);
		return ResponseEntity.status(HttpStatus.OK).body(event);
	}

	@PostMapping("/event")
	public ResponseEntity<Event> create(@RequestBody Map<String, String> eventData) {
		String name = eventData.get("name");
		String description = eventData.get("description");
		String place = eventData.get("place");
		String date = eventData.get("date");

		String pattern = "dd/MM/yyyy HH:mm";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		LocalDate localDate = LocalDate.parse(date, formatter);
		LocalDateTime dateTime = localDate.atStartOfDay();
		
		Event event = new Event(name, description, dateTime, place);
		Event newEvent = eventService.create(event);
		return ResponseEntity.status(HttpStatus.CREATED).body(newEvent);
	}

}
