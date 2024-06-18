package com.datacurationthesis.datacurationthesis.controller;

import com.datacurationthesis.datacurationthesis.entity.Venue;
import com.datacurationthesis.datacurationthesis.service.VenueService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

	@GetMapping("/limited")
	public Page<Venue> getLimitedVenues() {
		return venueService.getLimitedVenues(2);
	}

	@PutMapping("/update/{id}")
	public Venue updatVenue(@PathVariable Integer id, @RequestBody Venue venueDetails) {
		return venueService.updateVenue(id, venueDetails);
	}
}
