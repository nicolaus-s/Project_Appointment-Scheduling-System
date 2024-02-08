package query;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

public class AppointmentQuery {

    private static ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    private static ObservableList<Appointment> upcomingAppointments = FXCollections.observableArrayList();
    private static ObservableList<String> typeList = FXCollections.observableArrayList();

    /**Database query to retrieve all appointments and returns an ObservableList of the appointments.
     *
     * @return
     */
    public static ObservableList<Appointment> setAppointmentList() {
        try {
            appointmentList.clear();
            String sql = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID FROM client_schedule.appointments;";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int aID = rs.getInt("Appointment_ID");
                String aTitle = rs.getString("Title");
                String aDesc = rs.getString("Description");
                String aLoc = rs.getString("Location");
                String aType = rs.getString("Type");
                LocalDateTime aStart = TimeHandler.getLocalDate(rs.getTimestamp("Start"));
                LocalDateTime aEnd = TimeHandler.getLocalDate(rs.getTimestamp("End"));
                int customerID = rs.getInt("Customer_ID");
                int userID = rs.getInt("User_ID");
                int contactID = rs.getInt("Contact_ID");
                Appointment appointment = new Appointment(aID, aTitle, aDesc, aLoc, aType, aStart, aEnd, customerID, userID, contactID);
                appointmentList.add(appointment);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return appointmentList;
    }

    /**Queries the appointments table for any appointments that occur within 15 minutes.
     *
     * @return
     */
    public static ObservableList<Appointment> loginAppointmentCheck(){
        try {
            upcomingAppointments.clear();
            LocalDateTime tsNow = TimeHandler.getLocalDate(Timestamp.valueOf(LocalDateTime.now()));
            LocalDateTime tsSoon = TimeHandler.getLocalDate(Timestamp.valueOf(LocalDateTime.now().plusMinutes(15)));
            Timestamp now = TimeHandler.getUTCDate(LocalDateTime.now());
            Timestamp soon = TimeHandler.getUTCDate(LocalDateTime.now().plusMinutes(15));
            String sql = "SELECT Appointment_ID, Start FROM client_schedule.appointments WHERE Start > ? AND Start < ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setTimestamp(1, now);
            ps.setTimestamp(2, soon);
            System.out.println(now + " to " + soon);
            ResultSet rs = ps.executeQuery();
            System.out.println("Query Results");
                while (rs.next()) {
                    int aID = rs.getInt("Appointment_ID");
                    LocalDateTime aStart = TimeHandler.getLocalDate(rs.getTimestamp("Start"));
                    System.out.println("Appointment ID: " + aID + '\n' + "Start Time: " + aStart);
                    Appointment app = new Appointment(aID, aStart);
                    upcomingAppointments.add(app);
                }
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return upcomingAppointments;
    }

    /**Inserts a new appointment into the appointments table.
     *
     * @param title
     * @param desc
     * @param loc
     * @param type
     * @param start
     * @param end
     * @param cusID
     * @param uID
     * @param conID
     * @throws SQLException
     */
    public static void insert(String title, String desc, String loc, String type, LocalDateTime start, LocalDateTime end, int cusID, int uID, int conID) throws SQLException {
        Timestamp tsStart = Timestamp.valueOf(start);
        Timestamp tsEnd = Timestamp.valueOf(end);
        String sql = "INSERT INTO client_schedule.appointments(Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID) VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, desc);
        ps.setString(3, loc);
        ps.setString(4, type);
        ps.setTimestamp(5, tsStart);
        ps.setTimestamp(6, tsEnd);
        ps.setInt(7, cusID);
        ps.setInt(8, uID);
        ps.setInt(9, conID);
        ps.executeUpdate();
    }

    /**Updates an appointment by ID.
     *
     * @param aID
     * @param title
     * @param desc
     * @param loc
     * @param type
     * @param start
     * @param end
     * @param cusID
     * @param uID
     * @param conID
     * @throws SQLException
     */
    public static void update(String aID, String title, String desc, String loc, String type, LocalDateTime start, LocalDateTime end, int cusID, int uID, int conID) throws SQLException {
        Timestamp tsStart = Timestamp.valueOf(start);
        Timestamp tsEnd = Timestamp.valueOf(end);
        String sql = "UPDATE client_schedule.appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, desc);
        ps.setString(3, loc);
        ps.setString(4, type);
        ps.setTimestamp(5, tsStart);
        ps.setTimestamp(6, tsEnd);
        ps.setInt(7, cusID);
        ps.setInt(8, uID);
        ps.setInt(9, conID);
        ps.setString(10, aID);
        ps.executeUpdate();
    }

    /**Deletes an appointment by ID from the appointments table.
     *
     * @param id
     * @throws SQLException
     */
        public static void delete (String id) throws SQLException {
            String sql = "DELETE FROM client_schedule.appointments WHERE Appointment_ID = ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setString(1, id);
            ps.executeUpdate();
        }

    /**Deletes appointments that contain the customer ID.
     *
     * @param id
     * @throws SQLException
     */
    public static void deleteRelated (String id) throws SQLException {
            System.out.println("Deleting appointments for Customer ID: " + id);
            String sql = "DELETE FROM client_schedule.appointments WHERE Customer_ID = ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setString(1, id);
            ps.execute();
        }

    /**Sets a list of all appointment types.
     *
     * @return
     */
    public static ObservableList<String> setTypeList(){
        try{
            typeList.clear();
            String sql = "SELECT Type FROM client_schedule.appointments";
            PreparedStatement ps =JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String aType = rs.getString("Type");
                System.out.println("Current Type: " + aType);
                     typeList.add(aType);
            }


        }
        catch(SQLException e){
            System.out.println(e);
        }
        return typeList;
    }
    }