package query;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ContactQuery {
    private static ObservableList<String> contactNameList = FXCollections.observableArrayList();

    /**Queries the contact database to retrieve all contacts and returns an ObservableList.
     *
     * @return
     */
    public static ObservableList<String> setContactNameList(){
        try{
            contactNameList.clear();
            String sql = "SELECT Contact_Name FROM client_schedule.contacts";
            PreparedStatement ps =JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String cName = rs.getString("Contact_Name");
                contactNameList.add(cName);
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
        System.out.println("CURRENT LIST");
        System.out.println(contactNameList);
        return contactNameList;
    }

    /**Resolves a contact name to a contact ID.
     *
     * @param contactName
     * @return
     */
    public static int getContactID(String contactName){
        int id = 0;
        try {
            String sql = "SELECT Contact_ID FROM client_schedule.contacts WHERE Contact_Name = ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setString(1, contactName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getInt("Contact_ID");
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return id;
    }

    /**Resolves a contact ID to a contact name
     *
     * @param id
     * @return
     */
    public static String getContactByID(int id){
        String contactName = "NOT FOUND";
        try {
        String sql = "SELECT Contact_Name FROM client_schedule.contacts WHERE Contact_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            contactName = rs.getString("Contact_Name");
        }
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return contactName;
    }
}
