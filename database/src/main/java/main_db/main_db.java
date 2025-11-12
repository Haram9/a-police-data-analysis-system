package main_db;

import java.util.*;

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
        
      
        MenuHandler menu = new MenuHandler(records, scanner);
        menu.showMenu();
        
        scanner.close();
    }
}
