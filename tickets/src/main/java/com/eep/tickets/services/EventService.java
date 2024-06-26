package com.eep.tickets.services;

import com.eep.tickets.models.Event;
import com.eep.tickets.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Page<Event> getAll(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public Event getById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public Event create(Event event) {
        return eventRepository.save(event);
    }

    public void delete(Long id) {
        eventRepository.deleteById(id);
    }
}