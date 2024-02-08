package query;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public abstract class CustomerQuery {
private static ObservableList<Customer> customerList = FXCollections.observableArrayList();
private static ObservableList<String> customerNameList = FXCollections.observableArrayList();
public static ObservableList<PieChart.Data> countryCount = FXCollections.observableArrayList();

    /**Queries the database for all customers and returns an ObservableList.
     *
     * @return
     */
    public static ObservableList<Customer> setCustomerList(){
        try{
        customerList.clear();
        String sql = "SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID FROM client_schedule.customers";
        PreparedStatement ps =JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int cID = rs.getInt("Customer_ID");
            String cName = rs.getString("Customer_Name");
            String cAddress = rs.getString("Address");
            String cPostalCode = rs.getString("Postal_Code");
            String cPhone = rs.getString("Phone");
            String cDiv = DivisionQuery.getDivision(rs.getInt("Division_ID"));
            //int cDivID = rs.getInt("Division_ID");
            Customer customer = new Customer(cID, cName, cAddress, cPostalCode, cPhone, cDiv);
            customerList.add(customer);
        }
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return customerList;
    }

    /**Queries the database for all customer name and returns an ObservableList.
     *
     * @return
     */
    public static ObservableList<String> setCustomerNameList(){
        try{
            customerNameList.clear();
            String sql = "SELECT Customer_Name FROM client_schedule.customers";
            PreparedStatement ps =JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String cName = rs.getString("Customer_Name");
                customerNameList.add(cName);
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
        System.out.println("CURRENT LIST");
        System.out.println(customerList);
        return customerNameList;
    }

    /**Resolves a customer name to an ID.
     *
     * @param cusName
     * @return
     * @throws SQLException
     */
    public static int getCustomerID(String cusName) throws SQLException{
        int id = 0;
        String sql = "SELECT Customer_ID FROM client_schedule.customers WHERE Customer_Name = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, cusName);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            id = rs.getInt("Customer_ID");
            }
        return id;
    }

    /**Resolves a customer ID to a customer name.
     *
     * @param id
     * @return
     */
    public static String getCustomerByID(int id){
        String customerName = "NOT FOUND";
        try {
            String sql = "SELECT Customer_Name FROM client_schedule.customers WHERE Customer_ID = ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                customerName = rs.getString("Customer_Name");
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return customerName;
    }

    /**Queries for all customers and groups them by which country they live in.
     *
     * @return
     */
    public static ObservableList<PieChart.Data> getCustomerCountryCount(){
        try{
            String sql = "SELECT  co.Country, COUNT(Customer_ID) AS Count FROM client_schedule.customers cu JOIN client_schedule.first_level_divisions f ON cu.Division_ID = f.Division_ID JOIN client_schedule.countries co ON f.Country_ID = co.Country_ID GROUP BY co.Country_ID;";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String country = rs.getString("Country");
                int num = rs.getInt("Count");
                String label = country + " - " + num;
                System.out.println(country + ": " + num);
                PieChart.Data d = new PieChart.Data(label, num);
                countryCount.add(d);
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return countryCount;
    }

    /**Adds a new customer to the database.
     *
     * @param name
     * @param address
     * @param zipCode
     * @param phoneNumber
     * @param division
     * @return
     * @throws SQLException
     */
    public static int insert(String name, String address, String zipCode, String phoneNumber, String division) throws SQLException{
        int divId = DivisionQuery.getDivisionId(division);
        String sql = "INSERT INTO client_schedule.customers(Customer_Name, Address, Postal_Code, Phone, Division_ID) VALUES(?,?,?,?,?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, name);
        ps.setString(2, address);
        ps.setString(3, zipCode);
        ps.setString(4, phoneNumber);
        ps.setInt(5, divId);
        int rowsAffected = ps.executeUpdate();
        ResultSet result = ps.getGeneratedKeys();
        result.next();
        return result.getInt(1);
    }

    /**Updates an existing customer by ID.
     *
     * @param id
     * @param name
     * @param address
     * @param zipCode
     * @param phoneNumber
     * @param division
     * @throws SQLException
     */
    public static void update(String id, String name, String address, String zipCode, String phoneNumber, String division) throws SQLException{
        int divId = DivisionQuery.getDivisionId(division);
        String sql = "UPDATE client_schedule.customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ? WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, name);
        ps.setString(2, address);
        ps.setString(3, zipCode);
        ps.setString(4, phoneNumber);
        ps.setInt(5, divId);
        ps.setString(6, id);
        ps.executeUpdate();
    }

    /**Deletes a customer by ID.
     *
     * @param id
     * @throws SQLException
     */
    public static void delete(String id) throws SQLException{
        //FIXME Needs to check for any existing appointments, prompt for confirmation, delete appointments, then delete customer
        AppointmentQuery.deleteRelated(id);
        String sql = "DELETE FROM client_schedule.customers WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, id);
        ps.executeUpdate();
    }
}