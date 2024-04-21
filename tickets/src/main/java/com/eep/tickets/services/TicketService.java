package com.eep.tickets.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eep.tickets.models.Ticket;
import com.eep.tickets.repositories.TicketRepository;

@Service
public class TicketService {

	private final TicketRepository ticketRepository;

	@Autowired
	public TicketService(TicketRepository ticketRepository) {
		this.ticketRepository = ticketRepository;
	}

	public List<Ticket> getAll() {
		return ticketRepository.findAll();
	}

	public Ticket getById(Long id) {
		return ticketRepository.findById(id).orElse(null);
	}

	public Ticket create(Ticket ticket) {
		return ticketRepository.save(ticket);
	}
}
