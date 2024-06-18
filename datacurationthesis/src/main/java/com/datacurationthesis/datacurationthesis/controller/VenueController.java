package com.datacurationthesis.datacurationthesis.controller;

import com.datacurationthesis.datacurationthesis.entity.Venue;
import com.datacurationthesis.datacurationthesis.service.VenueService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/datacuration/venues")
public class VenueController {

	@Autowired
	private VenueService venueService;

	@GetMapping("/all")
	public List<Venue> getAllVenues() {
		return venueService.getAllVenues();
	}
}
