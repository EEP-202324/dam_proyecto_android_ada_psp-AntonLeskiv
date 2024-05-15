package com.eep.tickets.services;

import com.eep.tickets.models.Event;
import com.eep.tickets.models.Ticket;
import com.eep.tickets.models.User;
import com.eep.tickets.repositories.EventRepository;
import com.eep.tickets.repositories.TicketRepository;
import com.eep.tickets.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository, UserRepository userRepository, EventRepository eventRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }

    public Ticket getById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    public Page<Ticket> getTicketsByUserId(Long userId, Pageable pageable) {
        return ticketRepository.findAllByUserId(userId, pageable);
    }

    public Ticket create(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setEvent(event);

        return ticketRepository.save(ticket);
    }
}