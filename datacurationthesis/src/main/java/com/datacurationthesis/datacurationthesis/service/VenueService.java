package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.entity.Venue;
import com.datacurationthesis.datacurationthesis.logger.LoggerController;
import com.datacurationthesis.datacurationthesis.repository.VenueRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VenueService {

	@Autowired
	private VenueRepository venueRepository;

	@Autowired
	private EntityManager entityManager;

	public List<Venue> getAllVenues() {
		List<Venue> venues = venueRepository.findAll();
		return venues;
	}

	public Page<Venue> getLimitedVenues(int limit) {
		Pageable pageable = PageRequest.of(0, limit);
		return venueRepository.findAll(pageable);
	}

	public Venue updateVenue(Integer id, Venue venueDetails) {
		Optional<Venue> optionalVenue = venueRepository.findById(id);
		if (optionalVenue.isPresent()) {
			Venue venue = optionalVenue.get();
			venue.setTitle(venueDetails.getTitle());
			venue.setAddress(venueDetails.getAddress());
			venue.setSystemId(venueDetails.getSystemId());
			venue.setTimestamp(venueDetails.getTimestamp());
			venue.setClaimed(venueDetails.isClaimed());
			Venue updatedVenue = venueRepository.save(venue);
			return updatedVenue;
		} else {
			LoggerController.error("Venue not found!");
		}
		return null;
	}
}