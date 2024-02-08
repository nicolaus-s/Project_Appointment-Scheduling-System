package query;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**Represents the User database queries.
 *
 */
public abstract class UserQuery {
    public static int currentUser;
    public static ObservableList<String> allUsers = FXCollections.observableArrayList();

    /**Queries the Users table by the username and password that is entered.
     *
     * @param userName
     * @param password
     * @return
     * @throws SQLException
     */
    public static int select(String userName, String password) throws SQLException{
        currentUser = 0;
        String sql = "SELECT User_ID FROM users WHERE User_Name = ? AND Password = ?";
        PreparedStatement query = JDBC.connection.prepareStatement(sql);
        query.setString(1, userName);
        query.setString(2, password);
        ResultSet result = query.executeQuery();
        while(result.next()){
            currentUser = result.getInt("User_ID");
            System.out.println("User ID " + currentUser + " has logged in.");
            }
        return currentUser;
    }

    /**Queries the users database to resolve a username to a user ID
     *
     * @param userName
     * @return
     * @throws SQLException
     */
    public static int getUserID(String userName) throws SQLException{
        int id = 0;
        String sql = "SELECT User_ID FROM client_schedule.users WHERE User_Name = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, userName);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            id = rs.getInt("User_ID");
        }
        return id;
    }

    /**Queries the users database to resolve a user ID to a user name.
     *
     * @param userID
     * @return
     */
    public static String getUserName(int userID){
        String name = "Does Not Exist";
        try {
            String sql = "SELECT User_Name FROM client_schedule.users WHERE User_ID = ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                name = rs.getString("User_Name");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        return name;
    }

    /**Queries the users table for all users and returns a list.
     *
     * @return
     * @throws SQLException
     */
    public static ObservableList<String> getAllUsers(){
        try {
            allUsers.clear();
            String sql = "Select User_Name FROM client_schedule.users";
            PreparedStatement query = JDBC.connection.prepareStatement(sql);
            ResultSet result = query.executeQuery();
            while (result.next()) {
                String u = result.getString("User_Name");
                allUsers.add(u);
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        return allUsers;
    }
}
