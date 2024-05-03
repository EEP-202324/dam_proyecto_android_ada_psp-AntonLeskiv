package com.eep.tickets.repositories;

import com.eep.tickets.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

}
