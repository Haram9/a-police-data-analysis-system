package main_db;
import java.time.LocalDateTime;

public class StopSearchRecord {
    String type;
    LocalDateTime date;
    String gender;
    String ageRange;
    String selfDefinedEthnicity;
    String officerDefinedEthnicity;
    String legislation;
    String objectOfSearch;
    String outcome;
    Boolean outcomeLinked;
    Boolean removalOfClothing;
    Double latitude;
    Double longitude;

   
    public String toString() {
        return String.format("[%s] %s, %s, %s, %s, Legislation: %s, Outcome: %s",
            date, gender, ageRange, objectOfSearch, selfDefinedEthnicity, legislation, outcome);
    }
}
