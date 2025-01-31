package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.entity.Person;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class PersonCurationService {

    @Autowired
    private SpellCheckService spellCheckService;

    // Regex patterns για καθαρισμό
    private static final Pattern MULTIPLE_SPACES_PATTERN = Pattern.compile("\\s{2,}");
    private static final Pattern INVALID_CHARACTERS_PATTERN = Pattern.compile("[^\\p{L}\\s'-]");
    private static final Pattern INVALID_SPACING_PATTERN = Pattern.compile("\\b[a-zA-Zα-ωΑ-Ω]\\b");

    /**
     * Τυποποίηση και καθαρισμός των ονομάτων στον πίνακα Persons.
     */
    public Person curatePerson(Person person) {
        if (person.getFullname() != null) {
            String cleanedName = cleanAndNormalizeName(person.getFullname());
            person.setFullname(cleanedName);
        }
        return person;
    }

    /**
     * Καθαρίζει και τυποποιεί το όνομα.
     */
    private String cleanAndNormalizeName(String name) {
        // Αφαίρεση ειδικών χαρακτήρων
        name = INVALID_CHARACTERS_PATTERN.matcher(name).replaceAll("");
        // Καθαρισμός περιττών κενών
        name = MULTIPLE_SPACES_PATTERN.matcher(name).replaceAll(" ");
        // Αφαίρεση ακατάλληλων αποστάσεων γραμμάτων
        name = INVALID_SPACING_PATTERN.matcher(name).replaceAll("");
        // Τυποποίηση κεφαλαίων-πεζών
        name = capitalizeWords(name);

        // Έλεγχος ορθογραφίας
        if (!spellCheckService.isValidWord(name)) {
            String correctedName = spellCheckService.autoCorrect(name);
            System.out.printf("Διόρθωση ονόματος από '%s' σε '%s'%n", name, correctedName);
            name = correctedName;
        }

        return name.trim();
    }

    /**
     * Κεφαλαιοποιεί τις πρώτες λέξεις κάθε λέξης.
     */
    private String capitalizeWords(String input) {
        return Arrays.stream(input.split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    /**
     * Επεξεργασία όλων των εγγραφών του πίνακα Persons.
     */
    public void curateAllPersons(List<Person> persons) {
        persons.forEach(this::curatePerson);
    }
}

	

	