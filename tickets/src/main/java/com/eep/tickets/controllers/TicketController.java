package com.eep.tickets.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/tickets/user/{userId}")
    public Page<Ticket> getTicketsByUserId(@PathVariable Long userId, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ticketService.getTicketsByUserId(userId, pageable);
    }

    // POST METHODS
    @PostMapping("ticket")
    public ResponseEntity<Ticket> create(@RequestParam Long userId, @RequestParam Long eventId) {
        try {
            Ticket createdTicket = ticketService.create(userId, eventId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
