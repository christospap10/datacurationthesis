package com.datacurationthesis.datacurationthesis.service;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DoyValidator {
    private static final String DOY_LIST_PATH = "./doy/doy_list.csv"; // Path to the file containing DOY list.
    private static Set<String> validDoySet;

    // Load the DOY list into a Set for validation.
    public static void loadDoyList() {
        validDoySet = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(DOY_LIST_PATH))) {
            validDoySet = br.lines()
                    .skip(1) 
                    .map(String::trim)
                    .map(String::toUpperCase) 
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DOY list from file: " + DOY_LIST_PATH, e);
        }
    }

    // Validate the DOY field.
    public static boolean isValidDoy(String doy) {
        if (validDoySet == null) {
            loadDoyList(); 
        }
        return doy != null && validDoySet.contains(doy.trim().toUpperCase());
    }
}
