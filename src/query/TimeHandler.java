package query;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.function.DoubleSupplier;
import java.util.function.IntToDoubleFunction;

/**Represents the class in which all time conversions occur throughout the application.
 *
 */
public abstract class TimeHandler {
//Class will be used for time conversions
    public static ObservableList<Integer> hours = FXCollections.observableArrayList();
    public static ObservableList<Integer> minutes = FXCollections.observableArrayList();
    public static ObservableList<Appointment> apps = FXCollections.observableArrayList();
    public static ObservableList<Month> months = FXCollections.observableArrayList();
    public static final LocalTime easternOHour = LocalTime.of(8, 0);
    public static final LocalTime easternCHour = LocalTime.of(22, 0);

    /**Builds a LocalDateTime with a date, hour, and minute input.
     *
     * @param d
     * @param h
     * @param m
     * @return
     */
    public static ZonedDateTime buildLocalDate(LocalDate d, int h, int m){
        LocalTime time = LocalTime.of(h, m);
        LocalDateTime dateTime = LocalDateTime.of(d, time);
        return dateTime.atZone(ZoneId.systemDefault());
    }

    /**Converts a ZonedDateTime to UTC time.
     *
     * @param zdt
     * @return
     */
    public static LocalDateTime getUTCDate(ZonedDateTime zdt) {
        ZonedDateTime utcDateTime = zdt.withZoneSameInstant(ZoneId.of("UTC"));
        return utcDateTime.toLocalDateTime();
    }

    /**Converts a LocalDateTime to UTC time.
     *
     * @param ldt
     * @return
     */
    public static Timestamp getUTCDate(LocalDateTime ldt) {
        ZonedDateTime localDateTime = ldt.atZone(ZoneId.systemDefault());
        ZonedDateTime utcDateTime = localDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        return Timestamp.valueOf(utcDateTime.toLocalDateTime());
    }

    /**Converts a Timestamp from a database query to LocalDateTime.
     *
     * @param ts
     * @return
     */
    public static LocalDateTime getLocalDate(Timestamp ts){
        LocalDateTime lt = ts.toLocalDateTime();
        ZonedDateTime zdt = lt.atZone(ZoneId.of("UTC"));
        ZonedDateTime sysZdt = zdt.withZoneSameInstant(ZoneId.systemDefault());
        return sysZdt.toLocalDateTime();
        }

    /**Checks conflicts by customer, appointment start time, and appointment end time.
     *
     * @param cID
     * @param s
     * @param e
     * @return
     */
    public static boolean checkConflict(int cID, LocalDateTime s, LocalDateTime e){
        System.out.println("checkConflict Input********");
        System.out.println("s: " + s);
        System.out.println("e " + e);
        boolean conflict = false;

        try {
            String sql = "SELECT Start, End FROM client_schedule.appointments WHERE Customer_ID = ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, cID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDateTime start = rs.getTimestamp("Start").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("End").toLocalDateTime();
                Appointment app = new Appointment(start, end);
                apps.add(app);
            }
            for(int i = 0; i < apps.size(); i++){
                Appointment a = apps.get(i);
                if(s.isBefore(a.EndTime)){
                    if(e.isAfter(a.StartTime)){
                        conflict = true;
                    }

                }
            }
        }
        catch (SQLException sqlE){
            System.out.println(sqlE);
        }
        return conflict;
    }

    /**Checks conflicts by customer, appointment start time, and appointment end time, but excludes the current appointment ID. Excluding the current appointment ID from the conflict check ensures that the appointment being updated doesn't conflict with itself.
     *
     * @param cID
     * @param s
     * @param e
     * @return
     */
    public static boolean checkConflictOnEdit(int cID, String aID, LocalDateTime s, LocalDateTime e){
        System.out.println("checkConflict Input********");
        System.out.println("s: " + s);
        System.out.println("e " + e);
        boolean conflict = false;

        try {
            String sql = "SELECT Start, End FROM client_schedule.appointments WHERE Customer_ID = ? AND Appointment_ID != ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, cID);
            ps.setString(2, aID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDateTime start = rs.getTimestamp("Start").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("End").toLocalDateTime();
                Appointment app = new Appointment(start, end);
                apps.add(app);
            }
            for(int i = 0; i < apps.size(); i++){
                Appointment a = apps.get(i);
                if(s.isBefore(a.EndTime)){
                    if(e.isAfter(a.StartTime)){
                        conflict = true;
                    }

                }
            }
        }
        catch (SQLException sqlE){
            System.out.println(sqlE);
        }
        return conflict;
    }

    /**Checks if the input start time and end times are within business hours.
     *
     * @param s
     * @param e
     * @return
     */
    public static boolean isBusinessHours(ZonedDateTime s, ZonedDateTime e){
        boolean b;

        //Convert appointment start to Eastern time
        ZonedDateTime easternStart = s.withZoneSameInstant(ZoneId.of("America/New_York"));
        //Extract hour
        LocalTime easternStartTime = easternStart.toLocalTime();

        //Convert appointment end to Eastern time
        ZonedDateTime easternEnd = e.withZoneSameInstant(ZoneId.of("America/New_York"));
        //Extract hour
        LocalTime easternEndTime = easternEnd.toLocalTime();

        //Date extractor
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        //Dates must match
        b =(easternStart.format(formatter).equals(easternEnd.format(formatter)));
        if(!b){
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Failed to create appointment.");
            error.setContentText("The appointment must start and end on the same day");
            error.showAndWait();
            return b;
        }
        //Start time can't be before open hours or after close hours
        b = b && !easternStartTime.isBefore(easternOHour) && !easternStartTime.isAfter(easternCHour);
        //End time can't be before open hours or after close hours
        b = b && !easternEndTime.isBefore(easternOHour) && !easternEndTime.isAfter(easternCHour);
        if(!b){
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Failed to create appointment.");
            error.setContentText("The appointment must start and end within business hours. Business hours are 0800 - 2200 Eastern.");
            error.showAndWait();
            return b;
        }
            return b;
    }

    /**Builds a list of hours and returns an ObservableList of these minutes.
     *
     * @return
     */
    public static ObservableList<Integer> setMinutesList(){
        minutes.clear();
        int i=0;
        while(i<=45){
            minutes.add(i);
            i = i + 15;
        }
        return minutes;
    }

    /**Builds a list of hours and returns an ObservableList of these hours.
     *
     * @return
     */
    public static ObservableList<Integer> setHoursList(){
        hours.clear();
        int i=0;
        while(i<24){
            hours.add(i);
            i++;
        }
        return hours;
    }

    /**Builds list of months (Jan - Dec) and returns an ObservableList of these months.
     *
     * @return
     */
    public static ObservableList<Month> setMonthsList(){
        months.clear();
        int i = 0;
        while(i < 12){
            i++;
            months.add(Month.of(i));
        }
        return months;
    }

}
