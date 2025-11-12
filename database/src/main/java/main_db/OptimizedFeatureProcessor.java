package main_db;


import java.util.*;
import java.util.stream.Collectors;

public class OptimizedFeatureProcessor {
    private List<StopSearchRecord> records;
    private Scanner scanner;
    
    // Indexes for O(1) lookups
    private Map<String, List<StopSearchRecord>> purposeIndex;
    private Map<String, List<StopSearchRecord>> ethnicityIndex;
    private Map<String, List<StopSearchRecord>> legislationIndex;
    private Map<String, List<StopSearchRecord>> genderIndex;
    private Map<String, List<StopSearchRecord>> ageRangeIndex;
    private Map<String, List<StopSearchRecord>> outcomeIndex;
    
    // Caches for frequent queries
    private Map<String, Map<String, Integer>> monthlyLegislationCache;
    private Map<String, Map<String, Integer>> monthlyEthnicityCache;
    private Map<String, String> mostFrequentLegislationCache;
    
    // Pre-sorted collections
    private List<StopSearchRecord> recordsSortedByDate;
    private Map<String, List<StopSearchRecord>> ethnicitySortedCache;

    public OptimizedFeatureProcessor(List<StopSearchRecord> records, Scanner scanner) {
        this.records = records;
        this.scanner = scanner;
        buildIndexes();
        buildCaches();
        preSortData();
    }

    /* ========== INDEX BUILDING ========== */
    private void buildIndexes() {
        System.out.println("Building indexes...");
        
        purposeIndex = new HashMap<>();
        ethnicityIndex = new HashMap<>();
        legislationIndex = new HashMap<>();
        genderIndex = new HashMap<>();
        ageRangeIndex = new HashMap<>();
        outcomeIndex = new HashMap<>();

        for (StopSearchRecord record : records) {
            // Index by purpose (O(1) lookup)
            if (!record.objectOfSearch.isEmpty()) {
                purposeIndex.computeIfAbsent(record.objectOfSearch.toLowerCase(), 
                    k -> new ArrayList<>()).add(record);
            }
            
            // Index by ethnicity (O(1) lookup)
            if (!record.selfDefinedEthnicity.isEmpty()) {
                ethnicityIndex.computeIfAbsent(record.selfDefinedEthnicity.toLowerCase(), 
                    k -> new ArrayList<>()).add(record);
            }
            
            // Index by legislation (O(1) lookup)
            if (!record.legislation.isEmpty()) {
                legislationIndex.computeIfAbsent(record.legislation.toLowerCase(), 
                    k -> new ArrayList<>()).add(record);
            }
            
            // Index by gender (O(1) lookup)
            if (!record.gender.isEmpty()) {
                genderIndex.computeIfAbsent(record.gender.toLowerCase(), 
                    k -> new ArrayList<>()).add(record);
            }
            
            // Index by age range (O(1) lookup)
            if (!record.ageRange.isEmpty()) {
                ageRangeIndex.computeIfAbsent(record.ageRange.toLowerCase(), 
                    k -> new ArrayList<>()).add(record);
            }
            
            // Index by outcome (O(1) lookup)
            if (!record.outcome.isEmpty()) {
                outcomeIndex.computeIfAbsent(record.outcome.toLowerCase(), 
                    k -> new ArrayList<>()).add(record);
            }
        }
    }

    /* ========== CACHE BUILDING ========== */
    private void buildCaches() {
        System.out.println("Building caches...");
        
        monthlyLegislationCache = new HashMap<>();
        monthlyEthnicityCache = new HashMap<>();
        mostFrequentLegislationCache = new HashMap<>();

        for (StopSearchRecord record : records) {
            if (record.date != null) {
                String monthKey = record.date.getMonthValue() + "-" + record.date.getYear();
                
                // Cache legislation counts by month
                if (!record.legislation.isEmpty()) {
                    monthlyLegislationCache
                        .computeIfAbsent(monthKey, k -> new HashMap<>())
                        .merge(record.legislation, 1, Integer::sum);
                }
                
                // Cache ethnicity counts by month
                if (!record.selfDefinedEthnicity.isEmpty()) {
                    monthlyEthnicityCache
                        .computeIfAbsent(monthKey, k -> new HashMap<>())
                        .merge(record.selfDefinedEthnicity, 1, Integer::sum);
                }
            }
        }
        
        // Precompute most frequent legislation per month
        for (String monthKey : monthlyLegislationCache.keySet()) {
            mostFrequentLegislationCache.put(monthKey, 
                findMaxKey(monthlyLegislationCache.get(monthKey)));
        }
    }

    /* ========== PRE-SORTING ========== */
    private void preSortData() {
        System.out.println("Pre-sorting data...");
        
        // Pre-sort all records by date
        recordsSortedByDate = new ArrayList<>(records);
        recordsSortedByDate.sort((a, b) -> {
            if (a.date == null) return 1;
            if (b.date == null) return -1;
            return b.date.compareTo(a.date); // Descending
        });
        
        // Pre-sort ethnicity records
        ethnicitySortedCache = new HashMap<>();
        for (Map.Entry<String, List<StopSearchRecord>> entry : ethnicityIndex.entrySet()) {
            List<StopSearchRecord> sorted = new ArrayList<>(entry.getValue());
            sorted.sort((a, b) -> {
                if (a.date == null) return 1;
                if (b.date == null) return -1;
                return b.date.compareTo(a.date); // Descending
            });
            ethnicitySortedCache.put(entry.getKey(), sorted);
        }
    }

    /* ========== OPTIMIZED FEATURE A (O(1)) ========== */
    public void featureA() {
        System.out.println("\n--- Distinct Search Purposes (Optimized) ---");
        System.out.println("Found " + purposeIndex.size() + " distinct purposes:");
        purposeIndex.keySet().forEach(purpose -> 
            System.out.println(" - " + purpose + " (" + purposeIndex.get(purpose).size() + " records)"));
    }

    /* ========== OPTIMIZED FEATURE B (O(1)) ========== */
    public void featureB() {
        System.out.println("\n--- Search by Purpose (Optimized) ---");
        
        // Show available purposes from index
        List<String> purposes = new ArrayList<>(purposeIndex.keySet());
        for (int i = 0; i < purposes.size(); i++) {
            System.out.println((i + 1) + ". " + purposes.get(i) + 
                " (" + purposeIndex.get(purposes.get(i)).size() + " records)");
        }

        System.out.print("Choose purpose number or name: ");
        String input = scanner.nextLine();
        String purpose;

        try {
            purpose = purposes.get(Integer.parseInt(input) - 1);
        } catch (Exception e) {
            purpose = input.toLowerCase();
        }

        // O(1) lookup instead of O(n) search
        List<StopSearchRecord> matches = purposeIndex.getOrDefault(purpose, new ArrayList<>());
        
        System.out.println("Found: " + matches.size() + " records (O(1) lookup)");
        System.out.print("Show details? (y/n): ");

        if (scanner.nextLine().equalsIgnoreCase("y")) {
            matches.forEach(this::printRecord);
        }
    }

    /* ========== OPTIMIZED FEATURE C (O(1) outcome lookup) ========== */
    public void featureC() {
        System.out.println("\n--- Outcome Analysis (Optimized) ---");

        // Use outcome index for faster filtering
        List<StopSearchRecord> successful = new ArrayList<>();
        List<StopSearchRecord> partlySuccessful = new ArrayList<>();
        List<StopSearchRecord> unsuccessful = new ArrayList<>();

        // Check outcomes from index first
        for (List<StopSearchRecord> outcomeRecords : outcomeIndex.values()) {
            for (StopSearchRecord record : outcomeRecords) {
                if (isSuccessful(record)) successful.add(record);
                else if (isPartlySuccessful(record)) partlySuccessful.add(record);
                else if (isUnsuccessful(record)) unsuccessful.add(record);
            }
        }

        System.out.println("Successful: " + successful.size() + " (using outcome index)");
        System.out.println("Partly: " + partlySuccessful.size());
        System.out.println("Unsuccessful: " + unsuccessful.size());

        System.out.print("Show which? (1/2/3/0): ");
        switch (scanner.nextLine()) {
            case "1": successful.forEach(this::printRecord); break;
            case "2": partlySuccessful.forEach(this::printRecord); break;
            case "3": unsuccessful.forEach(this::printRecord); break;
        }
    }

    /* ========== OPTIMIZED FEATURE D (O(1) cache lookup) ========== */
    public void featureD() {
        System.out.print("Enter month (1-12): ");
        int month = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Enter year (e.g., 2025): ");
        int year = Integer.parseInt(scanner.nextLine());
        
        String monthKey = month + "-" + year;

        // O(1) cache lookup instead of O(n) calculation
        Map<String, Integer> legislationCounts = monthlyLegislationCache.get(monthKey);
        
        if (legislationCounts == null) {
            System.out.println("No data for " + monthKey);
            return;
        }

        String topLegislation = mostFrequentLegislationCache.get(monthKey);
        System.out.println("Most frequent legislation: " + topLegislation + 
            " (" + legislationCounts.get(topLegislation) + " records) - (Cached result)");

        // Show successful legislation using cache
        Map<String, Integer> successfulCounts = new HashMap<>();
        for (StopSearchRecord record : records) {
            if (record.date != null && 
                record.date.getMonthValue() == month && 
                record.date.getYear() == year &&
                !record.legislation.isEmpty() && 
                isSuccessful(record)) {
                successfulCounts.merge(record.legislation, 1, Integer::sum);
            }
        }

        String topSuccessful = findMaxKey(successfulCounts);
        if (topSuccessful != null) {
            System.out.println("Most successful legislation: " + topSuccessful + 
                " (" + successfulCounts.get(topSuccessful) + " successful records)");
        }
    }

    /* ========== OPTIMIZED FEATURE E (O(1) cache lookup) ========== */
    public void featureE() {
        System.out.println("1. Month-wise (Cached)");
        System.out.println("2. Legislation-wise (Indexed)");
        System.out.print("Choose: ");

        switch (scanner.nextLine()) {
            case "1": ethnicByMonthOptimized(); break;
            case "2": ethnicByLawOptimized(); break;
            default: System.out.println("Invalid");
        }
    }

    private void ethnicByMonthOptimized() {
        System.out.print("Enter month: ");
        int month = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter year: ");
        int year = Integer.parseInt(scanner.nextLine());
        
        String monthKey = month + "-" + year;

        // O(1) cache lookup
        Map<String, Integer> ethnicityCounts = monthlyEthnicityCache.get(monthKey);
        
        if (ethnicityCounts == null) {
            System.out.println("No data for " + monthKey);
            return;
        }

        String topEthnicity = findMaxKey(ethnicityCounts);
        System.out.println("Highest ethnicity group: " + topEthnicity + 
            " (" + ethnicityCounts.get(topEthnicity) + " records) - (Cached result)");

        System.out.print("Show details? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            List<StopSearchRecord> ethnicRecords = ethnicityIndex.getOrDefault(
                topEthnicity.toLowerCase(), new ArrayList<>());
            ethnicRecords.forEach(this::printRecord);
        }
    }

    private void ethnicByLawOptimized() {
        // Show available legislations from index
        List<String> laws = new ArrayList<>(legislationIndex.keySet());
        for (int i = 0; i < laws.size(); i++) {
            System.out.println((i + 1) + ". " + laws.get(i) + 
                " (" + legislationIndex.get(laws.get(i)).size() + " records)");
        }

        System.out.print("Choose legislation: ");
        String input = scanner.nextLine();
        String law;

        try {
            law = laws.get(Integer.parseInt(input) - 1);
        } catch (Exception e) {
            law = input.toLowerCase();
        }

        // O(1) lookup for legislation records
        List<StopSearchRecord> lawRecords = legislationIndex.getOrDefault(law, new ArrayList<>());
        
        // Count ethnicities using indexed data
        Map<String, Integer> ethnicityCounts = new HashMap<>();
        for (StopSearchRecord record : lawRecords) {
            if (!record.selfDefinedEthnicity.isEmpty()) {
                ethnicityCounts.merge(record.selfDefinedEthnicity, 1, Integer::sum);
            }
        }

        String topEthnicity = findMaxKey(ethnicityCounts);
        if (topEthnicity != null) {
            System.out.println("Highest ethnicity for " + law + ": " + topEthnicity + 
                " (" + ethnicityCounts.get(topEthnicity) + " records) - (Indexed lookup)");
        }
    }

    /* ========== OPTIMIZED FEATURE F (O(1) pre-sorted) ========== */
    public void featureF() {
        System.out.println("\n--- Reverse Chronological by Ethnicity (Optimized) ---");

        // Show available ethnicities from index
        List<String> ethnicities = new ArrayList<>(ethnicityIndex.keySet());
        for (int i = 0; i < ethnicities.size(); i++) {
            System.out.println((i + 1) + ". " + ethnicities.get(i) + 
                " (" + ethnicityIndex.get(ethnicities.get(i)).size() + " records)");
        }

        System.out.print("Choose ethnicity: ");
        String input = scanner.nextLine();
        String ethnicity;

        try {
            ethnicity = ethnicities.get(Integer.parseInt(input) - 1);
        } catch (Exception e) {
            ethnicity = input.toLowerCase();
        }

        // O(1) lookup of pre-sorted records (no sorting needed!)
        List<StopSearchRecord> matches = ethnicitySortedCache.getOrDefault(ethnicity, new ArrayList<>());
        
        System.out.println("Found: " + matches.size() + " records (Pre-sorted, O(1) access)");
        matches.forEach(this::printRecord);
    }

    /* ========== OPTIMIZED FEATURE G (O(min(s1,s2,...)) ========== */
    public void featureG() {
        System.out.println("\n--- Custom Multi-Attribute Search (Optimized) ---");

        System.out.print("Gender: "); String gender = scanner.nextLine();
        System.out.print("Age range: "); String ageRange = scanner.nextLine();
        System.out.print("Ethnicity: "); String ethnicity = scanner.nextLine();
        System.out.print("Outcome: "); String outcome = scanner.nextLine();
        System.out.print("Object: "); String object = scanner.nextLine();

        // Start with smallest index for optimal performance
        List<StopSearchRecord> results = null;
        String smallestIndex = findSmallestIndex(gender, ageRange, ethnicity, outcome, object);
        
        switch (smallestIndex) {
            case "gender": 
                results = genderIndex.getOrDefault(gender.toLowerCase(), new ArrayList<>());
                break;
            case "ageRange":
                results = ageRangeIndex.getOrDefault(ageRange.toLowerCase(), new ArrayList<>());
                break;
            case "ethnicity":
                results = ethnicityIndex.getOrDefault(ethnicity.toLowerCase(), new ArrayList<>());
                break;
            case "outcome":
                results = outcomeIndex.getOrDefault(outcome.toLowerCase(), new ArrayList<>());
                break;
            case "object":
                results = purposeIndex.getOrDefault(object.toLowerCase(), new ArrayList<>());
                break;
            default:
                results = new ArrayList<>(records);
        }

        // Filter results from smallest set
        List<StopSearchRecord> finalResults = results.stream()
            .filter(r -> gender.isEmpty() || r.gender.equalsIgnoreCase(gender))
            .filter(r -> ageRange.isEmpty() || r.ageRange.equalsIgnoreCase(ageRange))
            .filter(r -> ethnicity.isEmpty() || r.selfDefinedEthnicity.toLowerCase().contains(ethnicity.toLowerCase()))
            .filter(r -> outcome.isEmpty() || r.outcome.toLowerCase().contains(outcome.toLowerCase()))
            .filter(r -> object.isEmpty() || r.objectOfSearch.toLowerCase().contains(object.toLowerCase()))
            .collect(Collectors.toList());

        System.out.println("Matches: " + finalResults.size() + " (Optimized filtering)");
        System.out.print("Show? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            finalResults.forEach(this::printRecord);
        }
    }

    private String findSmallestIndex(String gender, String ageRange, String ethnicity, String outcome, String object) {
        Map<String, Integer> sizes = new HashMap<>();
        
        if (!gender.isEmpty()) sizes.put("gender", genderIndex.getOrDefault(gender.toLowerCase(), new ArrayList<>()).size());
        if (!ageRange.isEmpty()) sizes.put("ageRange", ageRangeIndex.getOrDefault(ageRange.toLowerCase(), new ArrayList<>()).size());
        if (!ethnicity.isEmpty()) sizes.put("ethnicity", ethnicityIndex.getOrDefault(ethnicity.toLowerCase(), new ArrayList<>()).size());
        if (!outcome.isEmpty()) sizes.put("outcome", outcomeIndex.getOrDefault(outcome.toLowerCase(), new ArrayList<>()).size());
        if (!object.isEmpty()) sizes.put("object", purposeIndex.getOrDefault(object.toLowerCase(), new ArrayList<>()).size());
        
        return sizes.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("all");
    }

    /* ========== HELPER METHODS ========== */
    private boolean isSuccessful(StopSearchRecord r) {
        String o = r.outcome.toLowerCase();
        boolean positive = o.contains("arrest") || o.contains("caution") || o.contains("penalty") || o.contains("warning");
        return positive && r.outcomeLinked != null && r.outcomeLinked;
    }

    private boolean isPartlySuccessful(StopSearchRecord r) {
        String o = r.outcome.toLowerCase();
        boolean positive = o.contains("arrest") || o.contains("caution") || o.contains("penalty") || o.contains("warning");
        return positive && (r.outcomeLinked == null || !r.outcomeLinked);
    }

    private boolean isUnsuccessful(StopSearchRecord r) {
        String o = r.outcome.toLowerCase();
        return o.contains("no further action") || o.isEmpty();
    }

    private String findMaxKey(Map<String, Integer> map) {
        return map.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private void printRecord(StopSearchRecord r) {
        System.out.println("\nDate: " + (r.date != null ? r.date : "Unknown"));
        System.out.println("Gender: " + r.gender);
        System.out.println("Age: " + r.ageRange);
        System.out.println("Ethnicity: " + r.selfDefinedEthnicity);
        System.out.println("Object: " + r.objectOfSearch);
        System.out.println("Legislation: " + r.legislation);
        System.out.println("Outcome: " + r.outcome);
        System.out.println("Type: " + r.type);
        System.out.println("---");
    }
}
