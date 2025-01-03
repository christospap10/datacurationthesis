package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.controller.OllamaController;
import com.datacurationthesis.datacurationthesis.dto.SpellCheckResponse;
import com.datacurationthesis.datacurationthesis.entity.Contribution;
import com.datacurationthesis.datacurationthesis.entity.Event;
import com.datacurationthesis.datacurationthesis.entity.Organizer;
import com.datacurationthesis.datacurationthesis.entity.Person;
import com.datacurationthesis.datacurationthesis.entity.Production;
import com.datacurationthesis.datacurationthesis.entity.Role;
import com.datacurationthesis.datacurationthesis.entity.Venue;
import com.datacurationthesis.datacurationthesis.logger.LoggerController;
import com.datacurationthesis.datacurationthesis.repository.OrganizerRepository;
import com.datacurationthesis.datacurationthesis.repository.VenueRepository;
import com.datacurationthesis.datacurationthesis.util.StringUtils;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Autowired
    private OllamaController ollamaController;

    private static final Pattern MULTIPLE_SPACES_PATTERN = Pattern.compile("\\s{2,}");
    private static final Pattern SPECIAL_CHARACTERS_PATTERN = Pattern.compile("[^\\p{L}\\p{N}\\s]");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\d{10}|\\d{12})$");
    private static final Pattern INVALID_LETTER_PATTERN = Pattern.compile("\\s+[a-zA-Zα-ωΑ-Ω]\\s+");
    @Autowired
    private OrganizerRepository organizerRepository;
    @Autowired
    private VenueRepository venueRepository;

    public String normalizeString(String str) {
        return str.trim().replaceAll(SPECIAL_CHARACTERS_PATTERN.pattern(), "")
                .replaceAll(MULTIPLE_SPACES_PATTERN.pattern(), " ");
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

        // Clean and curate the name field
        if (organizer.getName() != null) {
            mergeDuplicates(organizer);
            String name = normalizeString(organizer.getName());
            name = StringUtils.capitalizeWords(name);
            LoggerController.formattedInfo("START: Normalized name: %s", name);

            String possibleNameReplacement = null;
            LoggerController.formattedInfo("Spell checked name: %s",
                    name + " - " + spellCheckService.isValidWord(name));

            // Remove special characters, random letters with spaces, and multiple spaces
            name = SPECIAL_CHARACTERS_PATTERN.matcher(name).replaceAll("");
            name = INVALID_LETTER_PATTERN.matcher(name).replaceAll(" ");
            name = MULTIPLE_SPACES_PATTERN.matcher(name).replaceAll(" ");

            entities = nlpService.extractEntities(name);
            LoggerController.formattedInfo("Person Extracted entities: %s", entities);

            // Apply spell check and correction
            if (!spellCheckService.isValidWord(name)) {
                logInvalidWord(name, "Organizer", "name");
                possibleNameReplacement = spellCheckService.autoCorrect(name);
                LoggerController.formattedInfo("Organizer name corrected by spell checking service to: %s",
                        possibleNameReplacement);
                name = possibleNameReplacement;
                organizer.setName(name.trim());
            }
            if (spellCheckService.isEnglishText(name)) {
                LoggerController.formattedInfo("English word detected: %s", name);
                possibleNameReplacement = spellCheckService.autoCorrectEnglish(name);
                LoggerController.formattedInfo("Organizer name corrected by spell checking service to: %s",
                        possibleNameReplacement);
                name = possibleNameReplacement;
                organizer.setName(name.trim());
            }
            if (spellCheckService.isGreekText(name)) {
                LoggerController.info("Applying Levenshtein distance");
                SpellCheckResponse levenshteinResponse = greekSpellCkeckerService.checkAndSuggestSentence(name);
                String levenshteinSuggestion = levenshteinResponse.getLevenshteinSuggestion();
                LoggerController.formattedInfo("Organizer name corrected by Levenshtein distance to: %s",
                        levenshteinSuggestion);

                LoggerController.info("Applying Hamming distance algorithm!");
                String hammingDistanceSuggestion = levenshteinService.hammingDistanceForSentence(name);
                LoggerController.formattedInfo("Organizer name corrected by Hamming distance to: %s",
                        hammingDistanceSuggestion);

                System.out.println("Choose a correction for the organizer name:");
                System.out.printf("1. Levenshtein suggestion: %s\n", levenshteinSuggestion);
                System.out.printf("2. Hamming distance suggestion: %s\n", hammingDistanceSuggestion);
                System.out.print(
                        "Enter the number of the suggestion you want to apply (or press enter to keep the original): ");

                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine().trim();

                if (!input.isEmpty()) {
                    if (input.equals("1")) {
                        name = levenshteinSuggestion;
                        LoggerController.formattedInfo("Organizer name corrected by Levenshtein distance to: %s", name);
                    } else if (input.equals("2")) {
                        name = hammingDistanceSuggestion;
                        LoggerController.formattedInfo("Organizer name corrected by Hamming distance to: %s", name);
                    } else {
                        System.out.print("Enter your custom correction (or press enter to keep the original): ");
                        String customCorrection = scanner.nextLine().trim();
                        if (!customCorrection.isEmpty()) {
                            name = customCorrection;
                            LoggerController.formattedInfo("Organizer name manually corrected to: %s", name);
                        } else {
                            LoggerController.formattedInfo("No corrections applied. Keeping original name: %s", name);
                        }
                    }
                } else {
                    LoggerController.formattedInfo("No corrections applied. Keeping original name: %s", name);
                }
            }

            LoggerController.formattedInfo("Finally curated name: %s", name);
            organizer.setName(StringUtils.capitalizeWords(name.trim()));
        }

        // Clean and curate the address field
        if (organizer.getAddress() != null) {
            String address = organizer.getAddress();
            address = normalizeAddressString(address);
            address = StringUtils.capitalizeWords(address);
            LoggerController.formattedInfo("START: Normalized address: %s", address);

            String possibleAddressReplacement = null;
            LoggerController.formattedInfo("Spell checked address: %s",
                    address + " - " + spellCheckService.isValidWord(address));
            entities = nlpService.extractEntities(address);
            LoggerController.formattedInfo("Organizer Address Extracted entities: %s", entities);

            if (!spellCheckService.isValidWord(address)) {
                logInvalidWord(address, "Organizer", "address");
                possibleAddressReplacement = spellCheckService.autoCorrect(address);
                LoggerController.formattedInfo("Organizer address corrected by spell checking service to: %s",
                        possibleAddressReplacement);
                address = possibleAddressReplacement;
                organizer.setAddress(address.trim());
            }
            if (spellCheckService.isGreekText(address)) {
                LoggerController.info("Applying Levenshtein distance");
                SpellCheckResponse levenshteinResponse = greekSpellCkeckerService.checkAndSuggestSentence(address);
                String levenshteinSuggestion = levenshteinResponse.getLevenshteinSuggestion();
                LoggerController.formattedInfo("Organizer address corrected by Levenshtein distance to: %s",
                        levenshteinSuggestion);

                LoggerController.info("Applying Hamming distance algorithm!");
                String hammingDistanceSuggestion = levenshteinService.hammingDistanceForSentence(address);
                LoggerController.formattedInfo("Organizer address corrected by Hamming distance to: %s",
                        hammingDistanceSuggestion);

                System.out.println("Choose a correction for the organizer address:");
                System.out.printf("1. Levenshtein suggestion: %s\n", levenshteinSuggestion);
                System.out.printf("2. Hamming distance suggestion: %s\n", hammingDistanceSuggestion);
                System.out.print(
                        "Enter the number of the suggestion you want to apply (or press enter to keep the original): ");

                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine().trim();

                if (!input.isEmpty()) {
                    if (input.equals("1")) {
                        address = levenshteinSuggestion;
                        LoggerController.formattedInfo("Organizer address corrected by Levenshtein distance to: %s",
                                address);
                    } else if (input.equals("2")) {
                        address = hammingDistanceSuggestion;
                        LoggerController.formattedInfo("Organizer address corrected by Hamming distance to: %s",
                                address);
                    } else {
                        System.out.print("Enter your custom correction (or press enter to keep the original): ");
                        String customCorrection = scanner.nextLine().trim();
                        if (!customCorrection.isEmpty()) {
                            address = customCorrection;
                            LoggerController.formattedInfo("Organizer address manually corrected to: %s", address);
                        } else {
                            LoggerController.formattedInfo("No corrections applied. Keeping original address: %s",
                                    address);
                        }
                    }
                } else {
                    LoggerController.formattedInfo("No corrections applied. Keeping original address: %s", address);
                }
            }

            LoggerController.formattedInfo("Finally curated address: %s", address);
            organizer.setAddress((address.trim()));
        }

        // Clean and curate the town field
        if (organizer.getTown() != null) {
            organizer.setTown(normalizeString(organizer.getTown()));
            String town = normalizeString(organizer.getTown());
            entities = nlpService.extractEntities(town);
            LoggerController.formattedInfo("Organizer Town Extracted entities: %s", entities);

            String possibleTownReplacement = null;
            if (!spellCheckService.isValidWord(town)) {
                logInvalidWord(town, "Organizer", "town");
                possibleTownReplacement = spellCheckService.autoCorrect(town);
                LoggerController.formattedInfo("Organizer town corrected by spell checking service to: %s",
                        possibleTownReplacement);
                town = possibleTownReplacement;
                organizer.setTown(town.trim());
            }

            if (spellCheckService.isGreekText(town)) {
                LoggerController.info("Applying Levenshtein distance");
                SpellCheckResponse levenshteinResponse = greekSpellCkeckerService.checkAndSuggestSentence(town);
                String levenshteinSuggestion = levenshteinResponse.getLevenshteinSuggestion();
                LoggerController.formattedInfo("Organizer town corrected by Levenshtein distance to: %s",
                        levenshteinSuggestion);
                town = levenshteinSuggestion;
            }
            organizer.setTown(StringUtils.capitalizeWords(town.trim()));
        }

        // Validate and clean other fields
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

    public List<Venue> cleanVenueData(List<Venue> venues) {
        return venues.stream().map(this::cleanVenue).collect(Collectors.toList());
    }

    public void cleanAllVenues() {
        List<Venue> allVenues = venueRepository.findAll();
        List<Venue> processedVenues = cleanVenueData(allVenues);
        venueRepository.saveAll(processedVenues);
        LoggerController.formattedInfo("Processed venues: %s", processedVenues);
    }

    public Venue cleanVenue(Venue venue) {
        Map<String, List<String>> entities = new HashMap<>();

        if (venue.getTitle() != null) {
            mergeDuplicates(venue);
            // Normalize the string
            if (venue.getTitle().contains("&")) {
                venue.setTitle(venue.getTitle().replace("&", "και"));
            }
            String title = normalizeString(venue.getTitle());
            title = StringUtils.capitalizeWords(title);
            LoggerController.formattedInfo("START: Normalized title: %s", title);
            String possibleTitleReplacement = null;

            // Spell check
            LoggerController.formattedInfo("Spell checked title: %s",
                    title + " - " + spellCheckService.isValidWord(title));
            // Remove all special characters
            title = SPECIAL_CHARACTERS_PATTERN.matcher(title).replaceAll("");
            // Remove random letter with spaces around it
            title = INVALID_LETTER_PATTERN.matcher(title).replaceAll(" ");
            // Remove any multiple spaces that might have been introduced
            title = MULTIPLE_SPACES_PATTERN.matcher(title).replaceAll(" ");

            // Entity extraction
            entities = nlpService.extractEntities(title);
            LoggerController.formattedInfo("Venue Extracted entities: %s", entities);

            // Spell check and correction
            if (!spellCheckService.isValidWord(title)) {
                logInvalidWord(title, "Venue", "title");
                possibleTitleReplacement = spellCheckService.autoCorrect(title);
                LoggerController.formattedInfo("Venue title corrected by spell checking service to: %s",
                        possibleTitleReplacement);
                title = possibleTitleReplacement;
                venue.setTitle(title.trim());
            }
            if (spellCheckService.isEnglishText(title)) {
                LoggerController.formattedInfo("English word detected: %s", title);
                possibleTitleReplacement = spellCheckService.autoCorrectEnglish(title);
                LoggerController.formattedInfo("Venue title corrected by spell checking service to: %s",
                        possibleTitleReplacement);
                title = possibleTitleReplacement;
                venue.setTitle(title.trim());
            }
            if (spellCheckService.isGreekText(title)) {
                LoggerController.info("Applying Levenshtein distance");
                SpellCheckResponse levenshteinResponse = greekSpellCkeckerService.checkAndSuggestSentence(title);
                String levenshteinSuggestion = levenshteinResponse.getLevenshteinSuggestion();
                LoggerController.formattedInfo("Venue title corrected by Levenshtein distance to: %s",
                        levenshteinSuggestion);

                // Applying Hamming distance
                LoggerController.info("Applying Hamming distance algorithm!");
                String hammingDistanceSuggestion = levenshteinService.hammingDistanceForSentence(title);
                LoggerController.formattedInfo("Venue title corrected by Hamming distance to: %s",
                        hammingDistanceSuggestion);

                // Compare suggestions and ask the user to choose
                System.out.println("Choose a correction for the venue title:");
                System.out.printf("1. Levenshtein suggestion: %s\n", levenshteinSuggestion);
                System.out.printf("2. Hamming distance suggestion: %s\n", hammingDistanceSuggestion);
                System.out.print(
                        "Enter the number of the suggestion you want to apply (or press enter to keep the original): ");

                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine().trim();

                if (!input.isEmpty()) {
                    if (input.equals("1")) {
                        title = levenshteinSuggestion;
                        LoggerController.formattedInfo("Venue title corrected by Levenshtein distance to: %s", title);
                    } else if (input.equals("2")) {
                        title = hammingDistanceSuggestion;
                        LoggerController.formattedInfo("Venue title corrected by Hamming distance to: %s", title);
                    } else {
                        System.out.print("Enter your custom correction (or press enter to keep the original): ");
                        String customCorrection = scanner.nextLine().trim();
                        if (!customCorrection.isEmpty()) {
                            title = customCorrection;
                            LoggerController.formattedInfo("Venue title manually corrected to: %s", title);
                        } else {
                            LoggerController.formattedInfo("No corrections applied. Keeping original title: %s", title);
                        }
                    }
                } else {
                    LoggerController.formattedInfo("No corrections applied. Keeping original title: %s", title);
                }
            }

            LoggerController.formattedInfo("Finally curated title: %s", title);
            // Set the title
            venue.setTitle(title.trim());
        }

        if (!venue.getAddress().isEmpty()) {
            // Normalize the string
            String address = venue.getAddress();
            address = StringUtils.capitalizeWords(address);
            LoggerController.formattedInfo("START: Normalized address: %s", address);
            String possibleAddressReplacement = null;

            // Spell check
            LoggerController.formattedInfo("Spell checked address: %s",
                    address + " - " + spellCheckService.isValidWord(address));
            // Remove random letter with spaces around it
            address = INVALID_LETTER_PATTERN.matcher(address).replaceAll(" ");
            // Remove any multiple spaces that might have been introduced
            address = MULTIPLE_SPACES_PATTERN.matcher(address).replaceAll(" ");

            // Entity extraction
            entities = nlpService.extractEntities(address);
            LoggerController.formattedInfo("Venue Address Extracted entities: %s", entities);

            // Spell check and correction
            if (!spellCheckService.isValidWord(address)) {
                logInvalidWord(address, "Venue", "address");
                possibleAddressReplacement = spellCheckService.autoCorrect(address);
                LoggerController.formattedInfo("Venue address corrected by spell checking service to: %s",
                        possibleAddressReplacement);
                address = possibleAddressReplacement;
                venue.setAddress(address.trim());
            }
            if (spellCheckService.isEnglishText(address)) {
                LoggerController.formattedInfo("English word detected: %s", address);
                possibleAddressReplacement = spellCheckService.autoCorrectEnglish(address);
                LoggerController.formattedInfo("Venue address corrected by spell checking service to: %s",
                        possibleAddressReplacement);
                address = possibleAddressReplacement;
                venue.setAddress(address.trim());
            }
            if (spellCheckService.isGreekText(address)) {
                LoggerController.info("Applying Levenshtein distance");
                SpellCheckResponse levenshteinResponse = greekSpellCkeckerService.checkAndSuggestSentence(address);
                String levenshteinSuggestion = levenshteinResponse.getLevenshteinSuggestion();
                LoggerController.formattedInfo("Venue address corrected by Levenshtein distance to: %s",
                        levenshteinSuggestion);

                // Applying Hamming distance
                LoggerController.info("Applying Hamming distance algorithm!");
                String hammingDistanceSuggestion = levenshteinService.hammingDistanceForSentence(address);
                LoggerController.formattedInfo("Venue address corrected by Hamming distance to: %s",
                        hammingDistanceSuggestion);

                // Compare suggestions and ask the user to choose
                System.out.println("Choose a correction for the venue address:");
                System.out.printf("1. Levenshtein suggestion: %s\n", levenshteinSuggestion);
                System.out.printf("2. Hamming distance suggestion: %s\n", hammingDistanceSuggestion);
                System.out.print(
                        "Enter the number of the suggestion you want to apply (or press enter to keep the original): ");

                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine().trim();

                if (!input.isEmpty()) {
                    if (input.equals("1")) {
                        address = levenshteinSuggestion;
                        LoggerController.formattedInfo("Venue address corrected by Levenshtein distance to: %s",
                                address);
                    } else if (input.equals("2")) {
                        address = hammingDistanceSuggestion;
                        LoggerController.formattedInfo("Venue address corrected by Hamming distance to: %s", address);
                    } else {
                        System.out.print("Enter your custom correction (or press enter to keep the original): ");
                        String customCorrection = scanner.nextLine().trim();
                        if (!customCorrection.isEmpty()) {
                            address = customCorrection;
                            LoggerController.formattedInfo("Venue address manually corrected to: %s", address);
                        } else {
                            LoggerController.formattedInfo("No corrections applied. Keeping original address: %s",
                                    address);
                        }
                    }
                } else {
                    LoggerController.formattedInfo("No corrections applied. Keeping original address: %s", address);
                }
            }

            LoggerController.formattedInfo("Finally curated address: %s", address);
            // Set the address
            venue.setAddress(address.trim());
        } else if (venue.getAddress() == null || venue.getAddress().isEmpty()) {
            LoggerController.formattedInfo(
                    "Address is missing for venue: " + venue.getTitle() + "\nUsing LLM to find the address...");
            String extractedAddress = ollamaController.getOpenAIresponse(venue.getTitle());
            if (extractedAddress != null) {
                LoggerController.formattedInfo("Updating venue address to: %s", extractedAddress);
                venue.setAddress(extractedAddress);
            } else {
                LoggerController.formattedError("Failed to update address for venue: %s", venue.getTitle());
            }
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
            StringUtils.capitalizeWords(role.getRole1());
        }
        return role;
    }

    public List<Contribution> cleanContributionData(List<Contribution> contributions) {
        return contributions.stream().map(this::cleanContribution).collect(Collectors.toList());
    }

    public Contribution cleanSingleContribution(Contribution contribution) {
        return cleanContribution(contribution);
    }

    public Contribution cleanContribution(Contribution contribution) {
        if (contribution.getSubrole() != null && !contribution.getSubrole().isEmpty()) {
            contribution.setSubrole(normalizeString(contribution.getSubrole()));
        }

        if (contribution.getSubrole() == null || contribution.getSubrole().isEmpty()) {
            Production production = contribution.getProduction();
            String url = production.getUrl();
            Person person = contribution.getPerson();
            String fullname = person.getFullname();

            System.out.println("Production: " + production.toString());
            System.out.println("Production URL: " + url);
            System.out.println("Person: " + person.toString());
            System.out.println("Person fullname: " + fullname);

            // Fetch subroles from LLM
            Map<String, List<String>> subrolesMap = ollamaController.getSubrolesFromLLM(url);
            StringBuilder newSubrole = new StringBuilder();
            subrolesMap.forEach((role, names) -> {
                if (names.contains(fullname)) {
                    if (newSubrole.length() > 0) {
                        newSubrole.append(" , ");
                    }
                    newSubrole.append(role);
                }
            });

            if (newSubrole.length() > 0) {
                contribution.setSubrole(newSubrole.toString());
                System.out.println("Updated subRole with roles: " + newSubrole.toString());
            } else {
                System.out.println("The fullname does not exist in the subroles JSON response.");

            }
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

    public List<Production> cleanProductionData(List<Production> productions) {
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

    private Organizer mergeDuplicates(Organizer organizer) {
        List<Organizer> duplicates = organizerRepository.findByNameAndAddress(organizer.getName(),
                organizer.getAddress());

        if (duplicates.size() > 1) {
            Organizer primary = duplicates.stream().filter(duplcate -> !duplcate.getProductions().isEmpty()).findFirst()
                    .orElse(duplicates.get(0));

            for (Organizer duplicate : duplicates) {
                if (duplicate != primary) {
                    // Merge and delete if the duplicate has no active relations
                    if (duplicate.getProductions().isEmpty()) {
                        primary = mergeFields(primary, duplicate);
                        organizerRepository.delete(duplicate);
                        LoggerController.formattedInfo("Deleted duplicate organizer with ID: %d", duplicate.getId());
                    } else {
                        // If the duplicate has active relations, update its address to match the
                        // primary
                        LoggerController.formattedInfo(
                                "Skipped deletion for organizer with ID: %d due to active relations with Productions",
                                duplicate.getId());
                        duplicate.setAddress(primary.getAddress());
                        organizerRepository.save(duplicate);
                    }
                }
            }
            return organizerRepository.save(primary);
        } else {
            LoggerController.formattedInfo("No duplicates found for organizer: %s ", organizer.getName());
        }
        return organizer;
    }

    private Organizer mergeFields(Organizer primary, Organizer duplicate) {
        if (primary.getEmail() == null && duplicate.getEmail() != null) {
            primary.setEmail(duplicate.getEmail());
        }
        if (primary.getPhone() == null && duplicate.getEmail() != null) {
            primary.setPhone(duplicate.getPhone());
        }
        if (primary.getDoy() == null && duplicate.getDoy() != null) {
            primary.setDoy(duplicate.getDoy());
        }
        if (primary.getTown() == null && duplicate.getTown() != null) {
            primary.setTown(duplicate.getTown());
        }
        if (primary.getPostcode() == null && duplicate.getPostcode() != null) {
            primary.setPostcode(duplicate.getPostcode());
        }

        return primary;
    }

    public Venue mergeDuplicates(Venue venue) {
        List<Venue> duplicates = venueRepository.findByTitle(venue.getTitle());
        LoggerController.formattedInfo("Venue title: %s", venue.getTitle());
        LoggerController.formattedInfo("Venue address: %s", venue.getAddress());
        LoggerController.formattedInfo("Duplicate venues found: %d", duplicates.size());

        if (duplicates.size() > 1) {
            Venue primary = duplicates.stream()
                    .max(Comparator.comparingInt(v -> v.getAddress() != null ? v.getAddress().length() : 0))
                    .orElse(duplicates.get(0));
            LoggerController.formattedInfo("Primary venue found in mergeDuplicates method: %s", primary.getId());
            LoggerController.formattedInfo("Removing input venue from the duplicates: %s", venue.getId());
            duplicates.remove(venue);
            LoggerController.formattedInfo("Duplicate venues: %s", duplicates);
            for (Venue duplicate : duplicates) {
                if (duplicate != primary) {
                    if (duplicate.getEvents().isEmpty() && duplicate.getUserVenues().isEmpty()) {
                        primary = mergeFields(primary, duplicate);
                        venueRepository.delete(duplicate);
                        LoggerController.formattedInfo("Deleted duplicate venue with ID: %d", duplicate.getId());
                    } else {
                        LoggerController.formattedInfo(
                                "Skipped deletion for venue with ID: %d due to active\nrelations with Events or UserVenues",
                                duplicate.getId());
                    }
                    if (!duplicate.getEvents().isEmpty() || !duplicate.getUserVenues().isEmpty()) {
                        duplicate.setAddress(primary.getAddress());
                        venueRepository.save(duplicate);
                    }
                }
            }
            // Save the primary record after merging
            return venueRepository.save(primary);
        }
        return venue;
    }

    public Venue mergeFields(Venue primary, Venue duplicate) {
        if ((primary.getTitle() == null || primary.getTitle().isEmpty()) && duplicate.getTitle() != null) {
            primary.setTitle(duplicate.getTitle());
        }
        if ((primary.getAddress() == null || primary.getAddress().isEmpty()) && duplicate.getAddress() != null) {
            primary.setAddress(duplicate.getAddress());
        }
        if (primary.getSystemId() == null && duplicate.getSystemId() != null) {
            primary.setSystemId(duplicate.getSystemId());
        }
        if (primary.getTimestamp() == null && duplicate.getTimestamp() != null) {
            primary.setTimestamp(duplicate.getTimestamp());
        }
        if (!primary.isClaimed() && duplicate.isClaimed()) {
            primary.setClaimed(duplicate.isClaimed());
        }
        return primary;
    }
}
