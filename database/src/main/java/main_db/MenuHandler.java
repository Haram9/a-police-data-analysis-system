package main_db;

import java.util.*;

public class MenuHandler {

    private Scanner scanner;
    private FeatureProcessor basicProcessor;
    private OptimizedFeatureProcessor optimizedProcessor;
    private List<StopSearchRecord> records;

    public MenuHandler(List<StopSearchRecord> records, Scanner scanner) {
        this.scanner = scanner;
        this.records = records;
        this.basicProcessor = new FeatureProcessor(records, scanner);
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== Police Stop & Search Analysis ===");
            System.out.println("1. Basic Version (Linear Search - O(n))");
            System.out.println("2. Optimized Version (Indexed - O(1))");
            System.out.println("X. Exit");
            System.out.print("Choose implementation: ");

            String choice = scanner.nextLine().toUpperCase();

            switch (choice) {
                case "1": 
                    showBasicMenu(); 
                    break;
                case "2": 
                    showOptimizedMenu(); 
                    break;
                case "X": 
                    System.out.println("Goodbye!");
                    return;
                default: 
                    System.out.println("Invalid choice! Please enter 1, 2, or X.");
            }
        }
    }

    private void showBasicMenu() {
        while (true) {
            System.out.println("\n=== BASIC VERSION (Linear Search) ===");
            System.out.println("A. List distinct search purposes");
            System.out.println("B. Search by purpose");
            System.out.println("C. Analyze outcomes");
            System.out.println("D. Most frequent legislation");
            System.out.println("E. Ethnic analysis");
            System.out.println("F. Reverse chronological listing");
            System.out.println("G. Multi-attribute search");
            System.out.println("BACK. Return to main menu");
            System.out.print("Choose feature: ");

            String choice = scanner.nextLine().toUpperCase();

            switch (choice) {
                case "A": 
                    basicProcessor.featureA(); 
                    break;
                case "B": 
                    basicProcessor.featureB(); 
                    break;
                case "C": 
                    basicProcessor.featureC(); 
                    break;
                case "D": 
                    basicProcessor.featureD(); 
                    break;
                case "E": 
                    basicProcessor.featureE(); 
                    break;
                case "F": 
                    basicProcessor.featureF(); 
                    break;
                case "G": 
                    basicProcessor.featureG(); 
                    break;
                case "BACK": 
                    return;
                default: 
                    System.out.println("Invalid choice! Please enter A-G or BACK.");
            }
        }
    }

    private void showOptimizedMenu() {
        if (optimizedProcessor == null) {
            System.out.println("Building indexes and caches...");
            optimizedProcessor = new OptimizedFeatureProcessor(records, scanner);
        }

        while (true) {
            System.out.println("\n=== OPTIMIZED VERSION (Indexed & Cached) ===");
            System.out.println("A. List distinct search purposes (O(1))");
            System.out.println("B. Search by purpose (O(1) indexed)");
            System.out.println("C. Analyze outcomes (Indexed filtering)");
            System.out.println("D. Most frequent legislation (Cached results)");
            System.out.println("E. Ethnic analysis (Cached/Indexed)");
            System.out.println("F. Reverse chronological listing (Pre-sorted)");
            System.out.println("G. Multi-attribute search (Optimized filtering)");
            System.out.println("BACK. Return to main menu");
            System.out.print("Choose feature: ");

            String choice = scanner.nextLine().toUpperCase();

            switch (choice) {
                case "A": 
                    optimizedProcessor.featureA(); 
                    break;
                case "B": 
                    optimizedProcessor.featureB(); 
                    break;
                case "C": 
                    optimizedProcessor.featureC(); 
                    break;
                case "D": 
                    optimizedProcessor.featureD(); 
                    break;
                case "E": 
                    optimizedProcessor.featureE(); 
                    break;
                case "F": 
                    optimizedProcessor.featureF(); 
                    break;
                case "G": 
                    optimizedProcessor.featureG(); 
                    break;
                case "BACK": 
                    return;
                default: 
                    System.out.println("Invalid choice! Please enter A-G or BACK.");
            }
        }
    }
}
