package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.entity.Event;
import com.datacurationthesis.datacurationthesis.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EventService {
	@Autowired
	private EventRepository eventRepository;

	public Page<Event> getFirstTenEvents() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Event> events = eventRepository.findAll(pageable);
		return events;
	}

}
