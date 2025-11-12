package main_db;

import java.util.*;
import java.util.stream.Collectors;

public class main_db {
    
    public static void main(String[] args) {
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

        DataLoader loader = new DataLoader();
        List<StopSearchRecord> records = loader.loadFiles(files); 

        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Choose mode:");
        System.out.println("1. Normal Application");
        System.out.println("2. NetBeans Profiling Mode");
        System.out.print("Choice: ");
        
        String choice = scanner.nextLine();
        
        if (choice.equals("2")) {
            // Run profiling mode
            profileAllFeatures(records);
        } else {
            // Run normal application
            MenuHandler menu = new MenuHandler(records, scanner);
            menu.showMenu();
        }
        
        scanner.close();
    }
    
    // ADD THIS PROFILING METHOD - IT'S MISSING FROM YOUR CURRENT CODE//
    public static void profileAllFeatures(List<StopSearchRecord> records) {
        System.out.println("=== NETBEANS PROFILING MODE ===");
        System.out.println("Dataset size: " + records.size() + " records");
        System.out.println("Starting performance profiling...\n");
        
        // Create processors (Scanner is dummy for profiling)
        FeatureProcessor basic = new FeatureProcessor(records, new Scanner(System.in));
        OptimizedFeatureProcessor optimized = new OptimizedFeatureProcessor(records, new Scanner(System.in));
        
        System.out.println("🔄 Running Feature A (Distinct Purposes)...");
        for (int i = 0; i < 100; i++) {
            basic.featureA();
            optimized.featureA();
        }
        
        System.out.println("🔄 Running Feature B (Search by Purpose)...");
        for (int i = 0; i < 50; i++) {
            // Simulate search without user input
            simulateFeatureB(basic, optimized, "Controlled drugs");
            simulateFeatureB(basic, optimized, "Offensive weapons");
        }
        
        System.out.println("🔄 Running Feature C (Outcome Analysis)...");
        for (int i = 0; i < 80; i++) {
            basic.featureC();
            optimized.featureC();
        }
        
        System.out.println("🔄 Running Feature D (Legislation by Month)...");
        for (int i = 0; i < 60; i++) {
            simulateFeatureD(basic, optimized);
        }
        
        System.out.println("🔄 Running Feature F (Reverse Chronological)...");
        for (int i = 0; i < 40; i++) {
            simulateFeatureF(basic, optimized);
        }
        
        System.out.println("✅ PROFILING COMPLETE - Check NetBeans Profiler results!");
    }

    // ADD THESE HELPER METHODS FOR PROFILING
    private static void simulateFeatureB(FeatureProcessor basic, OptimizedFeatureProcessor optimized, String purpose) {
        // Basic version simulation
        int count = 0;
        for (StopSearchRecord r : basic.records) {
            if (r.objectOfSearch.equalsIgnoreCase(purpose)) count++;
        }
        
        // Optimized version simulation
        List<StopSearchRecord> results = optimized.getPurposeIndex().getOrDefault(purpose.toLowerCase(), new ArrayList<>());
    }

    private static void simulateFeatureD(FeatureProcessor basic, OptimizedFeatureProcessor optimized) {
        // Test for June 2025
        Map<String, Integer> freq = new HashMap<>();
        for (StopSearchRecord r : basic.records) {
            if (r.date != null && r.date.getMonthValue() == 6 && !r.legislation.isEmpty()) {
                freq.merge(r.legislation, 1, Integer::sum);
            }
        }
        
        Map<String, Integer> cached = optimized.getMonthlyLegislationCache().getOrDefault("6-2025", new HashMap<>());
    }

    private static void simulateFeatureF(FeatureProcessor basic, OptimizedFeatureProcessor optimized) {
        // Test for "White" ethnicity
        List<StopSearchRecord> basicResults = new ArrayList<>();
        for (StopSearchRecord r : basic.records) {
            if (r.selfDefinedEthnicity.toLowerCase().contains("white")) {
                basicResults.add(r);
            }
        }
        basicResults.sort((a, b) -> {
            if (a.date == null || b.date == null) return 0;
            return b.date.compareTo(a.date);
        });
        
        List<StopSearchRecord> optimizedResults = optimized.getEthnicitySortedCache().getOrDefault("white", new ArrayList<>());
    }
}