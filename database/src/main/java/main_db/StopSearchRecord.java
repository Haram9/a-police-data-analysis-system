package main_db;

import java.time.LocalDateTime;

public class StopSearchRecord {
	// Type of stop and search (e.g., "Person search", "Vehicle search")
    String type;
 // Date and time when the stop and search occurred
    LocalDateTime date;
    String gender;
    String ageRange;
 // Ethnicity as self-defined by the individual
    String selfDefinedEthnicity;
    String officerDefinedEthnicity;
 // The legislation under which the search was carried out
    String legislation;
    String objectOfSearch;
    String outcome;
 // Indicates whether the outcome was linked to the object of the search
    Boolean outcomeLinked;
    Boolean removalOfClothing;
    Double latitude;
    Double longitude;

    public StopSearchRecord(String type, LocalDateTime date, String gender, String ageRange,
                            String selfDefinedEthnicity, String officerDefinedEthnicity,
                            String legislation, String objectOfSearch, String outcome,
                            Boolean outcomeLinked, Boolean removalOfClothing,
                            Double latitude, Double longitude) {
        this.type = type;
        this.date = date;
        this.gender = gender;
        this.ageRange = ageRange;
        this.selfDefinedEthnicity = selfDefinedEthnicity;
        this.officerDefinedEthnicity = officerDefinedEthnicity;
        this.legislation = legislation;
        this.objectOfSearch = objectOfSearch;
        this.outcome = outcome;
        this.outcomeLinked = outcomeLinked;
        this.removalOfClothing = removalOfClothing;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    /**
     * Returns a formatted string representation of this record.
     * 
     * Example output:
     * [2020-01-01T10:30] Male, 18-24, Drugs, White, Legislation: Misuse of Drugs Act, Outcome: Arrest
     *
     * @return A readable summary of the stop and search record
     */

    public String toString() {
        return String.format("[%s] %s, %s, %s, %s, Legislation: %s, Outcome: %s",
                date, gender, ageRange, objectOfSearch, selfDefinedEthnicity, legislation, outcome);
    }
}
