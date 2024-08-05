package com.datacurationthesis.datacurationthesis.controller;

import com.datacurationthesis.datacurationthesis.entity.Organizer;
import com.datacurationthesis.datacurationthesis.logger.LoggerController;
import com.datacurationthesis.datacurationthesis.repository.*;
import com.datacurationthesis.datacurationthesis.service.DataCurationService;
import com.datacurationthesis.datacurationthesis.service.GreekSpellCkeckerService;
import com.datacurationthesis.datacurationthesis.service.LevenshteinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/datacuration/v1")
public class DataCurationController {

    @Autowired
    private DataCurationService dataCurationService;
    @Autowired
    private GreekSpellCkeckerService greekSpellCkeckerService;
    @Autowired
    private LevenshteinService levenshteinService;
    @Autowired
    private OrganizerRepository organizerRepository;
    @Autowired
    private ContibutionRepository contibutionRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private ProductionRepository productionRepository;


    @GetMapping("/organizer")
    public Organizer getOrganizer(@RequestParam Integer id) {
        List<Organizer> organizers = new ArrayList<>();
        Organizer organizer = organizerRepository.findById(id).get();
        Organizer organizer2 = organizerRepository.findById(id).get();
        organizers.add(organizer);
        organizers.add(organizer2);
        LoggerController.info("Before cleaning: " + organizer.toString());
        LoggerController.info("Before cleaning2: " + organizer2.toString());
        dataCurationService.cleanOrganizerData(organizers);
        LoggerController.info("Data cleaned organizer: " + organizer.toString());
        LoggerController.info("Data cleaned organizer2: " + organizer2.toString());
        return organizer;
    }

    @PutMapping("/organizer/update")
    public Organizer updateOrganizer(@RequestParam Integer id) {
        Organizer organizer = organizerRepository.findById(id).get();
        LoggerController.info("Before cleaning: " + organizer.toString());
        dataCurationService.cleanSingleOrganzerData(organizer);
        LoggerController.info("Data cleaned: " + organizer.toString());
        return organizerRepository.save(organizer);
    }

    @GetMapping("/testGreekService")
    public void testGreekService() {
        greekSpellCkeckerService.testGreekSpellCheck();
    }


    @GetMapping("/testLevenshtein")
    public void testLevenshtein() {
        levenshteinService.testLevenshtein();
    }
}
