package com.datacurationthesis.datacurationthesis.controller;

import com.datacurationthesis.datacurationthesis.entity.Event;
import com.datacurationthesis.datacurationthesis.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/datacuration/events")
public class EventController {

	@Autowired
	private EventService eventService;

	@GetMapping("/firstten")
	public Page<Event> firstTen() {
		Page<Event> events = eventService.getFirstTenEvents();
		return events;
	}

}
