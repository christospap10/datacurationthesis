package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.dto.SpellCheckResponse;
import com.datacurationthesis.datacurationthesis.entity.*;
import com.datacurationthesis.datacurationthesis.logger.LoggerController;
import com.datacurationthesis.datacurationthesis.repository.OrganizerRepository;
import com.datacurationthesis.datacurationthesis.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.System;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DataCurationService {

    @Autowired
    private NlpService nlpService;

    @Autowired
    private SpellCheckService spellCheckService;
    @Autowired
    private GreekSpellCkeckerService greekSpellCkeckerService;
    @Autowired
    private LevenshteinService levenshteinService;

    private static final Pattern MULTIPLE_SPACES_PATTERN = Pattern.compile("\\s{2,}");
    private static final Pattern SPECIAL_CHARACTERS_PATTERN = Pattern.compile("[^\\p{L}\\p{N}\\s]");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\d{10}|\\d{12})$");
    private static final Pattern INVALID_LETTER_PATTERN = Pattern.compile("\\s+[a-zA-Zα-ωΑ-Ω]\\s+");
    @Autowired
    private OrganizerRepository organizerRepository;

    public String normalizeString(String str) {
       return str.trim().replaceAll(SPECIAL_CHARACTERS_PATTERN.pattern(), "").replaceAll(MULTIPLE_SPACES_PATTERN.pattern(), " ");
    }

    public String normalizeAddressString(String str) {
        // Trim the string to remove leading and trailing spaces
        str = str.trim();
        // Convert to uppercase
        str = str.toUpperCase();
        // Replace multiple spaces with a single space
        str = str.replaceAll("\\s+", " ");
        // Return the normalized address string
        return str;
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

    public void cleanAllOrganizers() {
        List<Organizer> organizers = organizerRepository.findAll();
        List<Organizer> cleanedOrganizers = cleanOrganizerData(organizers);
        organizerRepository.saveAll(cleanedOrganizers);
    }

    public Organizer cleanSingleOrganzerData(Organizer organizer) {
        return cleanOrganizer(organizer);
    }

    private Organizer cleanOrganizer(Organizer organizer) {
        Map<String, List<String>> entities = new HashMap<>();
        if (organizer.getName() != null) {
            // Normalize the string
            String name = normalizeString(organizer.getName());
            name = StringUtils.capitalizeWords(name);
            LoggerController.formattedInfo("START: Normalized name: %s", name);
            String possibleNameReplacement = null;
            LoggerController.formattedInfo("Spell checked name: %s", name + " - " + spellCheckService.isValidWord(name));
            // Remove all special characters
            name = SPECIAL_CHARACTERS_PATTERN.matcher(name).replaceAll("");
            // Remove random letter with spaces around it
            name = INVALID_LETTER_PATTERN.matcher(name).replaceAll(" ");
            // Remove any multiple spaces that might have been introduced
            name = MULTIPLE_SPACES_PATTERN.matcher(name).replaceAll(" ");
            entities = nlpService.extractEntities(name);
            LoggerController.formattedInfo("Person Extracted entities: %s", entities);
            // Spell check
            if(!spellCheckService.isValidWord(name)) {
                logInvalidWord(name, "Organizer", "name");
                possibleNameReplacement = spellCheckService.autoCorrect(name);
                LoggerController.formattedInfo("Organizer name corrected by spell checking service to: %s", possibleNameReplacement);
                name = possibleNameReplacement;
                organizer.setName(name.trim().toUpperCase());
            }
            if (spellCheckService.isGreekText(name)) {
                LoggerController.info("Applying levenshtein distance");
                SpellCheckResponse response = greekSpellCkeckerService.checkAndSuggestSentence(name);
                name = response.getLevenshteinSuggestion();
                LoggerController.formattedInfo("Organizer name corrected by levenshtein distance to: %s", name);
            }
            // Applying Hamming distance
            LoggerController.info("Applying hamming distance algorithm!");
            String hammingDistanceSuggestion = levenshteinService.hammingDistanceForSentence(name);
            LoggerController.formattedInfo("Organizer name corrected by hamming distance to: %s", hammingDistanceSuggestion);

            System.out.printf("Do you want to apply the suggested name? (y/n): ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("y")) {
                name = hammingDistanceSuggestion;
                LoggerController.formattedInfo("Organizer name corrected by hamming distance to: %s", name);
            } else {
                LoggerController.formattedInfo("Hamming distance suggestion rejected!");
            }

            LoggerController.formattedInfo("Finally curated name: %s", name);
            // Set the name
            organizer.setName(name.trim().toUpperCase());
        }
        if (organizer.getAddress() != null) {
            String address = organizer.getAddress();
            LoggerController.formattedInfo("Spell checked address: %s", address + " - " + spellCheckService.isValidWord(address));
            entities = nlpService.extractEntities(address);
            LoggerController.formattedInfo("Organizer Address Extracted entities: %s", entities);
            // Spell check
            if (!spellCheckService.isValidWord(address)) {
                logInvalidWord(address, "Organizer", "address");
                address = spellCheckService.autoCorrect(address);
                LoggerController.formattedInfo("Organizer address corrected by spell checking service to: %s", address);
            }
            if (spellCheckService.isGreekText(address)) {
                LoggerController.info("Applying Levenshtein distance");
                SpellCheckResponse response = greekSpellCkeckerService.checkAndSuggestSentence(address);
                address = response.getLevenshteinSuggestion();
                LoggerController.formattedInfo("Organizer address corrected by Levenshtein distance to: %s", address);
            }
            organizer.setAddress(normalizeAddressString(address.trim()));
        }
        if (organizer.getTown() != null) {
            organizer.setTown(normalizeString(organizer.getTown()).toUpperCase());
            String town = normalizeString(organizer.getTown());
            entities = nlpService.extractEntities(town);
            LoggerController.formattedInfo("Organizer Town Extracted entities: %s", entities);
            organizer.setTown(town.trim());
        }
        if (organizer.getEmail() != null && !validateEmail(organizer.getEmail())) {
            organizer.setEmail("unknown");
        }
        if (organizer.getPhone() != null && !validatePhoneNumber(organizer.getPhone())) {
            organizer.setPhone("unknown");
        }
        if (organizer.getDoy() != null) {
            organizer.setDoy(normalizeAddressString(organizer.getDoy()).trim().toUpperCase());
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

   private void logInvalidWord(String word, String entity, String field) {
       LoggerController.info("Invalid word detected: " + word + " in " + entity + " entity, field: " + field);
   }
}
