package com.eep.tickets.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eep.tickets.models.Event;
import com.eep.tickets.repositories.EventRepository;

@Service
public class EventService {

	private final EventRepository eventRepository;

	@Autowired
	public EventService(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	public List<Event> getAll() {
		return eventRepository.findAll();
	}

	public Event getById(Long id) {
		return eventRepository.findById(id).orElse(null);
	}

	public Event create(Event event) {
		return eventRepository.save(event);
	}

}
