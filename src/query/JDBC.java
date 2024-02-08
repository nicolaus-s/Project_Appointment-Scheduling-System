package query;

import java.sql.Connection;
import java.sql.DriverManager;

/**Represents the MYSql database connection used throughout the application.
 *
 */
public abstract class JDBC {

    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    //private static final String jdbcURL = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER";
    private static final String jdbcURL = protocol + vendor + location + databaseName;
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String userName = "sqlUser";
    private static final String password = "Passw0rd!";
    public static Connection connection;

    /**Opens the database connection.
     *
     */
    public static void openConnection(){
        try{
            Class.forName(driver);
            connection = DriverManager.getConnection(jdbcURL, userName, password);
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**Closes the database connection.
     *
     */
    public static void closeConnection(){
        try{
            connection.close();
            System.out.println("Connection Closed");
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }

    }

}
