package query;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class Appointment {
    public int ID;
    public String Title;
    public String Description;
    public String Location;
    public String Type;
    public LocalDateTime StartTime;
    public LocalDateTime EndTime;
    public int CustomerID;
    public int UserID;
    public String Contact;

    /**Represents an existing appointment.
     *
     * @param id
     * @param title
     * @param desc
     * @param loc
     * @param type
     * @param start
     * @param end
     * @param cuID
     * @param uID
     * @param contact
     */
    public Appointment(int id, String title, String desc, String loc, String type, LocalDateTime start, LocalDateTime end, int cuID, int uID, int contact){
        ID = id;
        Title = title;
        Description = desc;
        Location = loc;
        Type = type;
        StartTime = start;
        EndTime = end;
        CustomerID = cuID;
        UserID = uID;
        Contact = ContactQuery.getContactByID(contact);
    }

    /**Represents a new appointment.
     *
     * @param title
     * @param desc
     * @param loc
     * @param type
     * @param start
     * @param end
     * @param cuID
     * @param uID
     * @param contact
     * @throws SQLException
     */
    public Appointment(String title, String desc, String loc, String type, LocalDateTime start, LocalDateTime end, int cuID, int uID, int contact) throws SQLException {
        Title = title;
        Description = desc;
        Location = loc;
        Type = type;
        StartTime = start;
        EndTime = end;
        CustomerID = cuID;
        UserID = uID;
        Contact = ContactQuery.getContactByID(contact);
    }

    /**Represents an appointment's start and end times to check for scheduling conflicts.
     *
     * @param start
     * @param end
     */
    public Appointment (LocalDateTime start, LocalDateTime end){
        StartTime = start;
        EndTime = end;
    }

    /**Represents an upcoming appointment that will be presented in an alert upon logging in.
     *
     * @param id
     * @param s
     */
    public Appointment (int id, LocalDateTime s){
        ID = id;
        StartTime = s;
    }

    public int getID() {
        return ID;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public String getLocation() {
        return Location;
    }

    public String getType() {
        return Type;
    }

    public LocalDateTime getStartTime() {
        return StartTime;
    }

    public LocalDateTime getEndTime() {
        return EndTime;
    }

    public int getCustomerID() {
        return CustomerID;
    }

    public int getUserID() {
        return UserID;
    }

    public String getContact() {return Contact;}

}
