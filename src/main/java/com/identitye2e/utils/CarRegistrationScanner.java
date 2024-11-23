package com.identitye2e.utils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CarRegistrationScanner {

    private final List<String> actualCarRegistrationNumbers;
    private final List<String> expectedCarRegistrationNumbers;

    public CarRegistrationScanner() {
        actualCarRegistrationNumbers = new ArrayList<>();
        expectedCarRegistrationNumbers = new ArrayList<>();
    }

    public void readCarRegistrationFromFile(String filePath) {

        String registrationPattern = "[A-Z]{2}\\d{2,3}\\s?[A-Z]{3}";
        Pattern pattern = Pattern.compile(registrationPattern);

        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Matcher matcher = pattern.matcher(line);

                while (matcher.find()) {
                    actualCarRegistrationNumbers.add(matcher.group());
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }

    public List<String> getActualCarRegistrationNumbers() {
        return actualCarRegistrationNumbers;
    }

    public List<String> getExpectedCarRegistrationNumbers() {
        return expectedCarRegistrationNumbers;
    }

    public void checkCarRegistrationAgainstExpectedOutput(String filePath) {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;

            while ((line = reader.readLine()) != null) {
                expectedCarRegistrationNumbers.add(line.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the reader when done
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
