package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DataCurationService {

    @Autowired
    private NlpService nlpService;

    private static final Pattern MULTIPLE_SPACES_PATTERN = Pattern.compile("\\s{2,}");
    private static final Pattern SPECIAL_CHARACTERS_PATTERN = Pattern.compile("[^\\p{L}\\p{N}\\s]");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

    public String normalizeString(String str) {
       return str.trim().replaceAll(SPECIAL_CHARACTERS_PATTERN.pattern(), "").replaceAll(MULTIPLE_SPACES_PATTERN.pattern(), " ");
    }

    public boolean validateEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean validatePhoneNumber(String phone) {
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public List<Organizer> cleanOrganizerData(List<Organizer> organizers) {
        return organizers.stream().map(this::cleanOrganizer).collect(Collectors.toList());
    }

    public Organizer cleanSingleOrganzerData(Organizer organizer) {
        return cleanOrganizer(organizer);
    }

    private Organizer cleanOrganizer(Organizer organizer) {
        if (organizer.getName() != null) {
           organizer.setName(normalizeString(organizer.getName()));
        }
        if (organizer.getName().contains("r")) {
            organizer.setName(normalizeString(organizer.getName().replace("r", "")));
        }
        if (organizer.getAddress() != null) {
           organizer.setAddress(normalizeString(organizer.getAddress()));
        }
        if (organizer.getEmail() != null && !validateEmail(organizer.getEmail())) {
            organizer.setEmail(null);
        }
        if (organizer.getPhone() != null && !validatePhoneNumber(organizer.getPhone())) {
            organizer.setPhone(null);
        }
        return organizer;
    }

   public List<Venue>  cleanVenueData(List<Venue> venues) {
        return venues.stream().map((this::cleanVenue)).collect(Collectors.toList());
   }

   private Venue cleanVenue(Venue venue) {
        if (venue.getTitle() != null) {
            venue.setTitle(normalizeString((venue.getTitle())));
        }
        if (venue.getAddress() != null) {
            venue.setAddress(normalizeString((venue.getAddress())));
        }
        return venue;
   }

   public List<Person> cleanPersonData(List<Person> people) {
        return people.stream().map(this::cleanPerson).collect(Collectors.toList());
   }

   private Person cleanPerson(Person person) {
       if (person.getFullname() != null) {
           person.setFullname(normalizeString(person.getFullname()));
       }
       return person;
   }

   public List<Role> cleanRoleData(List<Role> roles) {
        return roles.stream().map(this::cleanRole).collect(Collectors.toList());
   }

   private Role cleanRole(Role role) {
       if (role.getRole1() != null) {
           role.setRole1(normalizeString(role.getRole1()));
       }
       return role;
   }

   public List<Contribution> cleanContributionData(List<Contribution> contributions) {
        return contributions.stream().map(this::cleanContribution).collect(Collectors.toList());
   }

   private Contribution cleanContribution(Contribution contribution) {
       if (contribution.getSubRole() != null) {
           contribution.setSubRole(normalizeString(contribution.getSubRole()));
       }
       return contribution;
   }

   public List<Event> cleanEventData(List<Event> events) {
        return events.stream().map(this::cleanEvent).collect(Collectors.toList());
   }

   private Event cleanEvent(Event event) {
        if (event.getPriceRange() != null) {
            event.setPriceRange(normalizeString(event.getPriceRange()));
        }
        return event;
   }

   public  List<Production> cleanProductionData(List<Production> productions) {
        return productions.stream().map(this::cleanProduction).collect(Collectors.toList());
   }

   private Production cleanProduction(Production production) {
        if (production.getDescription() != null) {
            production.setDescription(normalizeString(production.getDescription()));
        }
        if (production.getProducer() != null) {
            production.setProducer(normalizeString(production.getProducer()));
        }
        if (production.getTitle() != null) {
            production.setTitle(normalizeString(production.getTitle()));
        }
        return production;
   }
}
