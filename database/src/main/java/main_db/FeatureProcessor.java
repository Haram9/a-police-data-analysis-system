package main_db;

import java.util.*;

public class FeatureProcessor {

    List<StopSearchRecord> records;
    private Scanner scanner;

    public FeatureProcessor(List<StopSearchRecord> records, Scanner scanner) {
        this.records = records;
        this.scanner = scanner;
    }

    /* ---------------------- FEATURE A ---------------------- */
    public void featureA() {
        System.out.println("\n--- Distinct Search Purposes ---");
        Set<String> set = new TreeSet<>();

        for (StopSearchRecord r : records)
            if (!r.objectOfSearch.isEmpty())
                set.add(r.objectOfSearch);

        set.forEach(System.out::println);
    }

    /* ---------------------- FEATURE B ---------------------- */
    public void featureB() {
        System.out.println("\n--- Search by Purpose ---");

        Set<String> purposes = new TreeSet<>();
        for (StopSearchRecord r : records)
            if (!r.objectOfSearch.isEmpty())
                purposes.add(r.objectOfSearch);

        List<String> list = new ArrayList<>(purposes);
        for (int i = 0; i < list.size(); i++)
            System.out.println((i + 1) + ". " + list.get(i));

        System.out.print("Choose: ");
        String input = scanner.nextLine();
        String purpose;

        try {
            purpose = list.get(Integer.parseInt(input) - 1);
        } catch (Exception e) {
            purpose = input;
        }

        List<StopSearchRecord> matches = new ArrayList<>();
        for (StopSearchRecord r : records)
            if (r.objectOfSearch.equalsIgnoreCase(purpose))
                matches.add(r);

        System.out.println("Found: " + matches.size());
        System.out.print("Show? (y/n): ");

        if (scanner.nextLine().equalsIgnoreCase("y"))
            matches.forEach(this::printRecord);
    }

    /* ---------------------- FEATURE C ---------------------- */
    public void featureC() {

        System.out.println("\n--- Outcome Analysis ---");

        int success = 0, partial = 0, fail = 0;
        List<StopSearchRecord> s1 = new ArrayList<>(), s2 = new ArrayList<>(), s3 = new ArrayList<>();

        for (StopSearchRecord r : records) {
            if (isSuccessful(r)) { success++; s1.add(r); }
            else if (isPartlySuccessful(r)) { partial++; s2.add(r); }
            else if (isUnsuccessful(r)) { fail++; s3.add(r); }
        }

        System.out.println("Successful: " + success);
        System.out.println("Partly: " + partial);
        System.out.println("Unsuccessful: " + fail);

        System.out.print("Show which? (1/2/3/0): ");
        switch (scanner.nextLine()) {
            case "1": s1.forEach(this::printRecord); break;
            case "2": s2.forEach(this::printRecord); break;
            case "3": s3.forEach(this::printRecord); break;
        }
    }

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

    /* ---------------------- FEATURE D ---------------------- */
    public void featureD() {

        System.out.print("Enter month (6-9): ");
        int month = Integer.parseInt(scanner.nextLine());

        Map<String, Integer> freq = new HashMap<>();
        for (StopSearchRecord r : records)
            if (r.date != null && r.date.getMonthValue() == month && !r.legislation.isEmpty())
                freq.merge(r.legislation, 1, Integer::sum);

        String top = findMax(freq);
        if (top == null) { System.out.println("No data."); return; }

        System.out.println("Most frequent legislation: " + top + " (" + freq.get(top) + ")");
    }

    /* ---------------------- FEATURE E ---------------------- */
    public void featureE() {
        System.out.println("1. Month-wise");
        System.out.println("2. Legislation-wise");
        System.out.print("Choose: ");

        switch (scanner.nextLine()) {
            case "1": ethnicByMonth(); break;
            case "2": ethnicByLaw(); break;
            default: System.out.println("Invalid");
        }
    }

    private void ethnicByMonth() {

        System.out.print("Enter month: ");
        int m = Integer.parseInt(scanner.nextLine());

        Map<String, Integer> map = new HashMap<>();
        for (StopSearchRecord r : records)
            if (r.date != null && r.date.getMonthValue() == m && !r.selfDefinedEthnicity.isEmpty())
                map.merge(r.selfDefinedEthnicity, 1, Integer::sum);

        String group = findMax(map);
        if (group == null) { System.out.println("No data."); return; }

        System.out.println("Highest: " + group + " (" + map.get(group) + ")");
    }

    private void ethnicByLaw() {

        Set<String> laws = new TreeSet<>();
        for (StopSearchRecord r : records)
            if (!r.legislation.isEmpty()) laws.add(r.legislation);

        List<String> list = new ArrayList<>(laws);
        for (int i = 0; i < list.size(); i++)
            System.out.println((i + 1) + ". " + list.get(i));

        System.out.print("Choose: ");
        String input = scanner.nextLine();
        String law;

        try { law = list.get(Integer.parseInt(input) - 1); }
        catch (Exception e) { law = input; }

        Map<String, Integer> map = new HashMap<>();
        for (StopSearchRecord r : records)
            if (r.legislation.equalsIgnoreCase(law) && !r.selfDefinedEthnicity.isEmpty())
                map.merge(r.selfDefinedEthnicity, 1, Integer::sum);

        String group = findMax(map);
        if (group == null) { System.out.println("No data."); return; }

        System.out.println("Highest: " + group + " (" + map.get(group) + ")");
    }

    /* ---------------------- FEATURE F ---------------------- */
    public void featureF() {

        System.out.println("\n--- Reverse Chronological by Ethnicity ---");

        Set<String> set = new TreeSet<>();
        for (StopSearchRecord r : records)
            if (!r.selfDefinedEthnicity.isEmpty())
                set.add(r.selfDefinedEthnicity);

        List<String> list = new ArrayList<>(set);
        for (int i = 0; i < list.size(); i++)
            System.out.println((i + 1) + ". " + list.get(i));

        System.out.print("Choose: ");
        String input = scanner.nextLine();
        String eth;

        try { eth = list.get(Integer.parseInt(input) - 1); }
        catch (Exception e) { eth = input; }

        List<StopSearchRecord> match = new ArrayList<>();
        for (StopSearchRecord r : records)
            if (r.selfDefinedEthnicity.equalsIgnoreCase(eth) ||
                    r.selfDefinedEthnicity.toLowerCase().contains(eth.toLowerCase()))
                match.add(r);

        match.sort((a, b) -> {
            if (a.date == null) return 1;
            if (b.date == null) return -1;
            return b.date.compareTo(a.date);
        });

        match.forEach(this::printRecord);
    }

    /* ---------------------- FEATURE G ---------------------- */
    public void featureG() {

        System.out.println("\n--- Custom Multi-Attribute Search ---");

        System.out.print("Gender: "); String g = scanner.nextLine();
        System.out.print("Age range: "); String a = scanner.nextLine();
        System.out.print("Ethnicity: "); String e = scanner.nextLine();
        System.out.print("Outcome: "); String o = scanner.nextLine();
        System.out.print("Object: "); String obj = scanner.nextLine();

        List<StopSearchRecord> results = new ArrayList<>();

        for (StopSearchRecord r : records) {
            boolean ok = true;
            
            String gender = r.gender.trim().toLowerCase();
            String ageRange = r.ageRange.trim().toLowerCase();
            String ethnicity = r.selfDefinedEthnicity.trim().toLowerCase();
            String outcome = r.outcome.trim().toLowerCase();
            String object = r.objectOfSearch.trim().toLowerCase();

            String g_ = g.trim().toLowerCase();
            String a_ = a.trim().toLowerCase();
            String e_ = e.trim().toLowerCase();
            String o_ = o.trim().toLowerCase();
            String obj_ = obj.trim().toLowerCase();

            if (!g_.isEmpty() && !gender.contains(g_)) ok = false;
            if (!a_.isEmpty() && !ageRange.contains(a_)) ok = false;
            if (!e_.isEmpty() && !ethnicity.contains(e_)) ok = false;
            if (!o_.isEmpty() && !outcome.contains(o_)) ok = false;
            if (!obj_.isEmpty() && !object.contains(obj_)) ok = false;



            if (ok) results.add(r);
        }


        System.out.println("Matches: " + results.size());

        System.out.print("Show? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y"))
            results.forEach(this::printRecord);
    }
    
    
    
    /* ---------------------- PROFILING VERSION (NO USER INPUT) ---------------------- */
    public void featureC_profiling() {

        int success = 0, partial = 0, fail = 0;
        
        for (StopSearchRecord r : records) {
            if (isSuccessful(r)) success++;
            else if (isPartlySuccessful(r)) partial++;
            else if (isUnsuccessful(r)) fail++;   
        }
    }

    public void featureB_profiling(String purpose) {
        int count = 0;
        for (StopSearchRecord r : records) {
            if (r.objectOfSearch.equalsIgnoreCase(purpose)) {
                count++;
            }
        }
       
    }

    
    /* ---------------------- UTILS ---------------------- */
    private String findMax(Map<String, Integer> map) {

        return map.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);
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
    }
}
