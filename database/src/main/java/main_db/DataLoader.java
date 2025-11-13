package main_db;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class DataLoader {
	/**
     * Loads Stop and Search data from multiple CSV files into memory.
     */

    public List<StopSearchRecord> loadFiles(String[] files) {

        List<StopSearchRecord> records = new ArrayList<>();
        int totalRecords = 0, failedRecords = 0;

        System.out.println("Loading Stop and Search Data...");
     // Loop through each provided file path
        for (String filename : files) {
            File file = new File(filename);
            // Skip missing files
            if (!file.exists()) {
                System.err.println("File not found: " + filename);
                continue;
            }
            // Try reading file line by line
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean first = true;

                while ((line = br.readLine()) != null) {
                    if (first) { first = false; continue; }

                    try {
                        StopSearchRecord record = parseCSVLine(line);
                        if (record != null) {
                            records.add(record);
                            totalRecords++;
                        }
                    } catch (Exception e) {
                        failedRecords++;
                    }
                }
            } catch (Exception ignore) {}
        }
     // Summary of loading results
        System.out.println("Loaded: " + totalRecords);
        System.out.println("Failed: " + failedRecords);
        System.out.println("Total in memory: " + records.size());

        return records;
    }

    private StopSearchRecord parseCSVLine(String line) {
    	// Split line by commas, ignoring commas inside quotes
        String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (fields.length < 15) return null;

        try {
            StopSearchRecord r = new StopSearchRecord(line, null, line, line, line, line, line, line, line, null, null, null, null);
         // Assign parsed values to record fields
            r.type = getField(fields, 0);
            r.date = parseDate(getField(fields, 1));
            r.gender = getField(fields, 6);
            r.ageRange = getField(fields, 7);
            r.selfDefinedEthnicity = getField(fields, 8);
            r.officerDefinedEthnicity = getField(fields, 9);
            r.legislation = getField(fields, 10);
            r.objectOfSearch = getField(fields, 11);
            r.outcome = getField(fields, 12);
            r.outcomeLinked = parseBoolean(getField(fields, 13));
            r.removalOfClothing = parseBoolean(getField(fields, 14));

            String lat = getField(fields, 4);
            String lon = getField(fields, 5);

            if (!lat.isEmpty() && !lon.isEmpty()) {
                try {
                    r.latitude = Double.parseDouble(lat);
                    r.longitude = Double.parseDouble(lon);
                } catch (Exception ignored) {}
            }

            return r;

        } catch (Exception e) {
            return null;
            
        }
    }
    /**
     * Safely retrieves a field value by index from an array.
     * Trims whitespace and handles missing fields.
     */
    private String getField(String[] f, int i) {
        if (i < f.length && !f[i].isEmpty()) return f[i].trim();
        return "";
    }

    private LocalDateTime parseDate(String d) {
        if (d.isEmpty()) return null;
        try { return LocalDateTime.parse(d.replace("+00:00", "")); }
        catch (Exception e) { return null; }
    }

    private Boolean parseBoolean(String b) {
        if (b.isEmpty()) return null;
        return b.equalsIgnoreCase("True");
    }
}
