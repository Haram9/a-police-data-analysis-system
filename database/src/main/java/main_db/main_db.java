package main_db;

import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class main_db {
    private List<StopSearchRecord> records;
    private Scanner scanner;
    
    public main_db() {
        this.records = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        main_db app = new main_db();
        app.run();
    }
    
    public void run() {
        loadData();
        showMenu();
    }
    
    private void loadData() {
        System.out.println("Loading Stop and Search Data...");
        
       
        String[] files = {
        	"C:\\Users\\DELL\\git\\a-police-data-analysis-system\\database\\src\\main\\java\\main_db\\2025-06-cheshire-stop-and-search.csv",
        	"C:\\Users\\DELL\\git\\a-police-data-analysis-system\\database\\src\\main\\java\\main_db\\2025-07-cheshire-stop-and-search.csv",
        	"C:\\Users\\DELL\\git\\a-police-data-analysis-system\\database\\src\\main\\java\\main_db\\2025-08-cheshire-stop-and-search.csv",
        	"C:\\Users\\DELL\\git\\a-police-data-analysis-system\\database\\src\\main\\java\\main_db\\2025-09-cheshire-stop-and-search.csv",
        	"C:\\Users\\DELL\\git\\a-police-data-analysis-system\\database\\src\\main\\java\\main_db\\2025-06-merseyside-stop-and-search.csv",
        	"C:\\Users\\DELL\\git\\a-police-data-analysis-system\\database\\src\\main\\java\\main_db\\2025-07-merseyside-stop-and-search.csv",
        	"C:\\Users\\DELL\\git\\a-police-data-analysis-system\\database\\src\\main\\java\\main_db\\2025-08-merseyside-stop-and-search.csv",
        	"C:\\Users\\DELL\\git\\a-police-data-analysis-system\\database\\src\\main\\java\\main_db\\2025-09-merseyside-stop-and-search.csv",
        };
        
        int totalRecords = 0;
        int failedRecords = 0;
        
        for (String filename : files) {
            File file = new File(filename);
            if (file.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    boolean isFirstLine = true;
                    
                    while ((line = br.readLine()) != null) {
                        if (isFirstLine) {
                            isFirstLine = false;
                            continue; // Skip header
                        }
                        
                        try {
                            StopSearchRecord record = parseCSVLine(line);
                            if (record != null) {
                                records.add(record);
                                totalRecords++;
                            }
                        } catch (Exception e) {
                            failedRecords++;
                            System.err.println("Failed to parse line: " + line);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Could not load file: " + filename);
                }
            } else {
                System.err.println("File not found: " + filename);
            }
        }
        
        System.out.println("Data loading completed!");
        System.out.println("Successfully loaded: " + totalRecords + " records");
        System.out.println("Failed to load: " + failedRecords + " records");
        System.out.println("Total records in memory: " + records.size());
    }
    
    private StopSearchRecord parseCSVLine(String line) {
      
        String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

        if (fields.length < 15) return null;

        StopSearchRecord record = new StopSearchRecord();
        try {
            record.type = getField(fields, 0);
            record.date = parseDate(getField(fields, 1));
            record.latitude = parseDouble(getField(fields, 4));
            record.longitude = parseDouble(getField(fields, 5));
            record.gender = getField(fields, 6);
            record.ageRange = getField(fields, 7);
            record.selfDefinedEthnicity = getField(fields, 8);
            record.officerDefinedEthnicity = getField(fields, 9);
            record.legislation = getField(fields, 10);
            record.objectOfSearch = getField(fields, 11);
            record.outcome = getField(fields, 12);
            record.outcomeLinked = parseBoolean(getField(fields, 13));
            record.removalOfClothing = parseBoolean(getField(fields, 14));
        } catch (Exception e) {
            System.err.println("Error parsing record: " + e.getMessage());
            return null;
        }
        return record;
    }

    private String getField(String[] fields, int index) {
        if (index < fields.length && fields[index] != null) {
            return fields[index].trim().replace("\"", "");
        }
        return "";
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            dateStr = dateStr.replace("+00:00", "");
            return LocalDateTime.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        try {
            if (value == null || value.isEmpty()) return null;
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean parseBoolean(String boolStr) {
        if (boolStr == null || boolStr.isEmpty()) return null;
        return boolStr.equalsIgnoreCase("TRUE") || boolStr.equalsIgnoreCase("Yes");
    }

    
    private void showMenu() {
        while (true) {
            System.out.println("\n=== Police Stop and Search Analysis System ===");
            System.out.println("A. List all distinct search purposes");
            System.out.println("B. Search by purpose");
            System.out.println("C. Analyze search outcomes");
            System.out.println("D. Find most frequent legislation");
            System.out.println("E. Ethnic group analysis");
            System.out.println("F. Reverse chronological listing");
            System.out.println("G. Custom multi-attribute search");
            System.out.println("X. Exit");
            System.out.print("Choose an option: ");
            
            String choice = scanner.nextLine().toUpperCase();
            
            switch (choice) {
                case "A": featureA(); break;
                case "B": featureB(); break;
                case "C": featureC(); break;
                case "D": featureD(); break;
                case "E": featureE(); break;
                case "F": featureF(); break;
                case "G": featureG(); break;
                case "X": 
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
  
    private void featureA() {
        System.out.println("\n--- Distinct Search Purposes ---");
        Set<String> purposes = new TreeSet<>();
        
        for (StopSearchRecord record : records) {
            if (!record.objectOfSearch.isEmpty()) {
                purposes.add(record.objectOfSearch);
            }
        }
        
        if (purposes.isEmpty()) {
            System.out.println("No search purposes found.");
        } else {
            System.out.println("Found " + purposes.size() + " distinct purposes:");
            for (String purpose : purposes) {
                System.out.println(" - " + purpose);
            }
        }
    }
    
    
    private void featureB() {
        System.out.println("\n--- Search by Purpose ---");
        
       
        Set<String> purposes = new TreeSet<>();
        for (StopSearchRecord record : records) {
            if (!record.objectOfSearch.isEmpty()) {
                purposes.add(record.objectOfSearch);
            }
        }
        
        System.out.println("Available search purposes:");
        List<String> purposeList = new ArrayList<>(purposes);
        for (int i = 0; i < purposeList.size(); i++) {
            System.out.println((i + 1) + ". " + purposeList.get(i));
        }
        
        System.out.print("Enter purpose number or type purpose name: ");
        String input = scanner.nextLine();
        
        String selectedPurpose;
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < purposeList.size()) {
                selectedPurpose = purposeList.get(index);
            } else {
                System.out.println("Invalid number.");
                return;
            }
        } catch (NumberFormatException e) {
            selectedPurpose = input;
        }
        
        List<StopSearchRecord> matchingRecords = new ArrayList<>();
        for (StopSearchRecord record : records) {
            if (record.objectOfSearch.equalsIgnoreCase(selectedPurpose)) {
                matchingRecords.add(record);
            }
        }
        
        System.out.println("\nFound " + matchingRecords.size() + " records for: " + selectedPurpose);
        System.out.print("Show details? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            for (StopSearchRecord record : matchingRecords) {
                printRecordDetails(record);
            }
        }
    }
    
    
    private void featureC() {
        System.out.println("\n--- Search Outcome Analysis ---");
        
        int successful = 0, partlySuccessful = 0, unsuccessful = 0;
        List<StopSearchRecord> successfulRecords = new ArrayList<>();
        List<StopSearchRecord> partlySuccessfulRecords = new ArrayList<>();
        List<StopSearchRecord> unsuccessfulRecords = new ArrayList<>();
        
        for (StopSearchRecord record : records) {
            if (isSuccessful(record)) {
                successful++;
                successfulRecords.add(record);
            } else if (isPartlySuccessful(record)) {
                partlySuccessful++;
                partlySuccessfulRecords.add(record);
            } else if (isUnsuccessful(record)) {
                unsuccessful++;
                unsuccessfulRecords.add(record);
            }
        }
        
        System.out.println("Successful searches: " + successful);
        System.out.println("Partly successful searches: " + partlySuccessful);
        System.out.println("Unsuccessful searches: " + unsuccessful);
        System.out.println("Total analyzed: " + records.size());
        
        System.out.print("\nShow details for which category? (1=successful, 2=partly, 3=unsuccessful, 0=none): ");
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                System.out.println("Successful searches details:");
                for (StopSearchRecord record : successfulRecords) {
                    printRecordDetails(record);
                }
                break;
            case "2":
                System.out.println("Partly successful searches details:");
                for (StopSearchRecord record : partlySuccessfulRecords) {
                    printRecordDetails(record);
                }
                break;
            case "3":
                System.out.println("Unsuccessful searches details:");
                for (StopSearchRecord record : unsuccessfulRecords) {
                    printRecordDetails(record);
                }
                break;
        }
    }
    
    private boolean isSuccessful(StopSearchRecord record) {
        String outcome = record.outcome.toLowerCase();
        boolean positiveOutcome = outcome.contains("arrest") || outcome.contains("caution") || 
                                 outcome.contains("penalty") || outcome.contains("warning");
        return positiveOutcome && record.outcomeLinked != null && record.outcomeLinked;
    }
    
    private boolean isPartlySuccessful(StopSearchRecord record) {
        String outcome = record.outcome.toLowerCase();
        boolean positiveOutcome = outcome.contains("arrest") || outcome.contains("caution") || 
                                 outcome.contains("penalty") || outcome.contains("warning");
        return positiveOutcome && (record.outcomeLinked == null || !record.outcomeLinked);
    }
    
    private boolean isUnsuccessful(StopSearchRecord record) {
        String outcome = record.outcome.toLowerCase();
        return outcome.contains("no further action") || outcome.isEmpty();
    }
    
  
    private void featureD() {
        System.out.println("\n--- Legislation Analysis ---");
        System.out.print("Enter month (1-12): ");
        int month;
        try {
            month = Integer.parseInt(scanner.nextLine());
            if (month < 1 || month > 12) {
                System.out.println("Invalid month.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        
       
        Map<String, Integer> legislationCount = new HashMap<>();
        for (StopSearchRecord record : records) {
            if (record.date != null && record.date.getMonthValue() == month && 
                !record.legislation.isEmpty()) {
                legislationCount.merge(record.legislation, 1, Integer::sum);
            }
        }
        
        String mostFrequent = findMaxKey(legislationCount);
        if (mostFrequent != null) {
            System.out.println("Most frequent legislation in month " + month + ": " + mostFrequent);
            System.out.println("Count: " + legislationCount.get(mostFrequent));
        } else {
            System.out.println("No data for month " + month);
            return;
        }
        
        
        Map<String, Integer> successfulLegislationCount = new HashMap<>();
        for (StopSearchRecord record : records) {
            if (record.date != null && record.date.getMonthValue() == month && 
                !record.legislation.isEmpty() && isSuccessful(record)) {
                successfulLegislationCount.merge(record.legislation, 1, Integer::sum);
            }
        }
        
        String mostSuccessful = findMaxKey(successfulLegislationCount);
        if (mostSuccessful != null) {
            System.out.println("Most successful legislation in month " + month + ": " + mostSuccessful);
            System.out.println("Successful count: " + successfulLegislationCount.get(mostSuccessful));
        }
    }
    
   
    private void featureE() {
        System.out.println("\n--- Ethnic Group Analysis ---");
        System.out.println("1. For specific month");
        System.out.println("2. For specific legislation");
        System.out.print("Choose option: ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                ethnicAnalysisByMonth();
                break;
            case "2":
                ethnicAnalysisByLegislation();
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    private void ethnicAnalysisByMonth() {
        System.out.print("Enter month (1-12): ");
        int month;
        try {
            month = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid month.");
            return;
        }
        
        Map<String, Integer> ethnicCount = new HashMap<>();
        for (StopSearchRecord record : records) {
            if (record.date != null && record.date.getMonthValue() == month &&
                !record.selfDefinedEthnicity.isEmpty()) {
                ethnicCount.merge(record.selfDefinedEthnicity, 1, Integer::sum);
            }
        }
        
        String highestEthnicGroup = findMaxKey(ethnicCount);
        if (highestEthnicGroup != null) {
            System.out.println("Ethnic group with highest stops in month " + month + ": " + highestEthnicGroup);
            System.out.println("Count: " + ethnicCount.get(highestEthnicGroup));
            
            System.out.print("Show details? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                for (StopSearchRecord record : records) {
                    if (record.selfDefinedEthnicity.equals(highestEthnicGroup) &&
                        record.date != null && record.date.getMonthValue() == month) {
                        printRecordDetails(record);
                    }
                }
            }
        } else {
            System.out.println("No data found for month " + month);
        }
    }
    
    private void ethnicAnalysisByLegislation() {
        
        Set<String> legislations = new TreeSet<>();
        for (StopSearchRecord record : records) {
            if (!record.legislation.isEmpty()) {
                legislations.add(record.legislation);
            }
        }
        
        System.out.println("Available legislations:");
        List<String> legislationList = new ArrayList<>(legislations);
        for (int i = 0; i < legislationList.size(); i++) {
            System.out.println((i + 1) + ". " + legislationList.get(i));
        }
        
        System.out.print("Enter legislation number or name: ");
        String input = scanner.nextLine();
        
        String selectedLegislation;
        try {
            int index = Integer.parseInt(input) - 1;
            selectedLegislation = legislationList.get(index);
        } catch (Exception e) {
            selectedLegislation = input;
        }
        
        Map<String, Integer> ethnicCount = new HashMap<>();
        for (StopSearchRecord record : records) {
            if (record.legislation.equalsIgnoreCase(selectedLegislation) &&
                !record.selfDefinedEthnicity.isEmpty()) {
                ethnicCount.merge(record.selfDefinedEthnicity, 1, Integer::sum);
            }
        }
        
        String highestEthnicGroup = findMaxKey(ethnicCount);
        if (highestEthnicGroup != null) {
            System.out.println("Ethnic group with highest stops for " + selectedLegislation + ": " + highestEthnicGroup);
            System.out.println("Count: " + ethnicCount.get(highestEthnicGroup));
            
            System.out.print("Show details? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                for (StopSearchRecord record : records) {
                    if (record.selfDefinedEthnicity.equals(highestEthnicGroup) &&
                        record.legislation.equalsIgnoreCase(selectedLegislation)) {
                        printRecordDetails(record);
                    }
                }
            }
        } else {
            System.out.println("No data found for legislation: " + selectedLegislation);
        }
    }
    
  
 
    private void featureF() {
        System.out.println("\n--- Reverse Chronological Order ---");
        
        
        Set<String> ethnicities = new TreeSet<>();
        for (StopSearchRecord record : records) {
            if (!record.selfDefinedEthnicity.isEmpty()) {
                ethnicities.add(record.selfDefinedEthnicity);
            }
        }
        
        System.out.println("Available ethnic groups:");
        List<String> ethnicityList = new ArrayList<>(ethnicities);
        for (int i = 0; i < ethnicityList.size(); i++) {
            System.out.println((i + 1) + ". " + ethnicityList.get(i));
        }
        
        System.out.print("Enter ethnicity number or name: ");
        String input = scanner.nextLine().trim();
        
        String selectedEthnicity;
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < ethnicityList.size()) {
                selectedEthnicity = ethnicityList.get(index);
            } else {
                System.out.println("Invalid number.");
                return;
            }
        } catch (NumberFormatException e) {
            selectedEthnicity = input;
        }
        
        List<StopSearchRecord> ethnicRecords = new ArrayList<>();
        for (StopSearchRecord record : records) {
            if (record.selfDefinedEthnicity != null && 
                record.selfDefinedEthnicity.equalsIgnoreCase(selectedEthnicity)) {
                ethnicRecords.add(record);
            }
        }
        
       
        if (ethnicRecords.isEmpty()) {
            System.out.println("No exact matches found. Trying partial match...");
            for (StopSearchRecord record : records) {
                if (record.selfDefinedEthnicity != null && 
                    record.selfDefinedEthnicity.toLowerCase().contains(selectedEthnicity.toLowerCase())) {
                    ethnicRecords.add(record);
                }
            }
        }
        
        if (ethnicRecords.isEmpty()) {
            System.out.println("No records found for ethnicity: " + selectedEthnicity);
            System.out.println("Available ethnicities are case-sensitive. Try one from the list above.");
            return;
        }
        
    
        ethnicRecords.sort((r1, r2) -> {
            if (r1.date == null && r2.date == null) return 0;
            if (r1.date == null) return 1;
            if (r2.date == null) return -1;
            return r2.date.compareTo(r1.date);
        });
        
        System.out.println("\nFound " + ethnicRecords.size() + " records for: " + selectedEthnicity);
        System.out.println("Displaying in reverse chronological order (newest first):\n");
        
        for (StopSearchRecord record : ethnicRecords) {
            printRecordDetails(record);
        }
    }
    
    
    private void featureG() {
        System.out.println("\n--- Custom Multi-Attribute Search ---");
        
        System.out.print("Enter gender (or press enter to skip): ");
        String gender = scanner.nextLine();
        System.out.print("Enter age range (or press enter to skip): ");
        String ageRange = scanner.nextLine();
        System.out.print("Enter ethnicity (or press enter to skip): ");
        String ethnicity = scanner.nextLine();
        System.out.print("Enter outcome (or press enter to skip): ");
        String outcome = scanner.nextLine();
        System.out.print("Enter object of search (or press enter to skip): ");
        String object = scanner.nextLine();
        
        List<StopSearchRecord> results = new ArrayList<>();
        for (StopSearchRecord record : records) {
            boolean matches = true;
            
            if (!gender.isEmpty() && !record.gender.equalsIgnoreCase(gender)) {
                matches = false;
            }
            if (!ageRange.isEmpty() && !record.ageRange.equalsIgnoreCase(ageRange)) {
                matches = false;
            }
            if (!ethnicity.isEmpty() && !record.selfDefinedEthnicity.toLowerCase().contains(ethnicity.toLowerCase())) {
                matches = false;
            }
            if (!outcome.isEmpty() && !record.outcome.toLowerCase().contains(outcome.toLowerCase())) {
                matches = false;
            }
            if (!object.isEmpty() && !record.objectOfSearch.toLowerCase().contains(object.toLowerCase())) {
                matches = false;
            }
            
            if (matches) {
                results.add(record);
            }
        }
        
        System.out.println("Found " + results.size() + " matching records");
        if (!results.isEmpty()) {
            System.out.print("Show details? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                for (StopSearchRecord record : results) {
                    printRecordDetails(record);
                }
            }
        }
    }
    
    private String findMaxKey(Map<String, Integer> map) {
        return map.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
    
    private void printRecordDetails(StopSearchRecord record) {
        System.out.println("Date: " + (record.date != null ? record.date : "Unknown"));
        System.out.println("  Gender: " + record.gender);
        System.out.println("  Age: " + record.ageRange);
        System.out.println("  Ethnicity: " + record.selfDefinedEthnicity);
        System.out.println("  Object: " + record.objectOfSearch);
        System.out.println("  Legislation: " + record.legislation);
        System.out.println("  Outcome: " + record.outcome);
        System.out.println("  Type: " + record.type);
        System.out.println("---");
    }
}