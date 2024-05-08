package com.eep.tickets.repositories;

import com.eep.tickets.models.Event;
import com.eep.tickets.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EventRepository extends JpaRepository<Event, Long>, PagingAndSortingRepository<Event, Long> {

}
