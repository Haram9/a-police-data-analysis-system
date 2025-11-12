package main_db;

import java.util.*;

public class main_db {

    public static void main(String[] args) {

        // Step 1: File paths
        String[] files = {
            "C:\\Users\\sadia\\git\\a-police-data-analysis-system\\database\\src\\main\\java\\main_db\\2025-06-south-yorkshire-stop-and-search.csv",
            "C:\\Users\\sadia\\git\\a-police-data-analysis-system\\database\\src\\main\\java\\main_db\\2025-07-south-yorkshire-stop-and-search.csv",
            "C:\\Users\\sadia\\git\\a-police-data-analysis-system\\database\\src\\main\\java\\main_db\\2025-08-south-yorkshire-stop-and-search.csv",
            "C:\\Users\\sadia\\git\\a-police-data-analysis-system\\database\\src\\main\\java\\main_db\\2025-09-south-yorkshire-stop-and-search.csv"
        };

        // Step 2: Load data using DataLoader
        DataLoader loader = new DataLoader();
        List<StopSearchRecord> records = loader.loadFiles(files);  // your existing method

        // Step 3: Pass records to MenuHandler
        MenuHandler menu = new MenuHandler(records);

        // Step 4: Show menu
        menu.showMenu();
    }
}
