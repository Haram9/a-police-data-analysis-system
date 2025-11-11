package main_db;

import java.util.*;

public class MenuHandler {

    private Scanner scanner;
    private FeatureProcessor processor;

    public MenuHandler(List<StopSearchRecord> records) {
        this.scanner = new Scanner(System.in);
        this.processor = new FeatureProcessor(records, scanner);
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== Police Stop & Search Analysis ===");
            System.out.println("A. List distinct search purposes");
            System.out.println("B. Search by purpose");
            System.out.println("C. Analyze outcomes");
            System.out.println("D. Most frequent legislation");
            System.out.println("E. Ethnic analysis");
            System.out.println("F. Reverse chronological listing");
            System.out.println("G. Multi-attribute search");
            System.out.println("X. Exit");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().toUpperCase();

            switch (choice) {
                case "A": processor.featureA(); break;
                case "B": processor.featureB(); break;
                case "C": processor.featureC(); break;
                case "D": processor.featureD(); break;
                case "E": processor.featureE(); break;
                case "F": processor.featureF(); break;
                case "G": processor.featureG(); break;
                case "X": return;
                default: System.out.println("Invalid choice!");
            }
        }
    }
}
