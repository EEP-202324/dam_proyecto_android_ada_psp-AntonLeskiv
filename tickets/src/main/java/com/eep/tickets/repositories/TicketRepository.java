package com.eep.tickets.repositories;

import com.eep.tickets.models.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long>, PagingAndSortingRepository<Ticket, Long> {
    Page<Ticket> findAllByUserId(Long userId, Pageable pageable);
}
