package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.entity.Venue;
import com.datacurationthesis.datacurationthesis.repository.VenueRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
}