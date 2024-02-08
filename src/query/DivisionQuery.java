package query;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DivisionQuery {
    private static ObservableList<String> countryList = FXCollections.observableArrayList();
    private static ObservableList<String> divisionList = FXCollections.observableArrayList();

    /**Queries the database for all countries and returns an ObservableList.
     *
     * @return
     */
    public static ObservableList<String> setCountryList(){
        try{
            countryList.clear();
            String sql = "SELECT Country FROM client_schedule.countries";
            PreparedStatement ps =JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String countryName = rs.getString("Country");
                countryList.add(countryName);
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
        System.out.println("COUNTRY LIST");
        System.out.println(countryList);
        return countryList;
    }

    /**Queries the database for all divisions (i.e. state, province) by country and returns an ObservableList.
     *
     * @param countryName
     * @return
     */
    public static ObservableList<String> setDivisonList(String countryName){
        try{
            divisionList.clear();
            String sql = "SELECT Division FROM client_schedule.first_level_divisions d JOIN client_schedule.countries c ON d.Country_ID = c.Country_ID WHERE Country = ?";
            PreparedStatement ps =JDBC.connection.prepareStatement(sql);
            ps.setString(1, countryName);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String divisionName = rs.getString("Division");
                divisionList.add(divisionName);
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
        System.out.println("Division LIST");
        System.out.println(divisionList);
        return divisionList;
    }

    /**Resolves a division to a division ID.
     *
     * @param division
     * @return
     */
    public static int getDivisionId(String division){
        int divId = 0;
        try {
            String sql = "SELECT Division_ID FROM client_schedule.first_level_divisions WHERE Division = ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setString(1, division);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                divId = rs.getInt("Division_ID");
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
        System.out.println("Division ID is " + divId);
        return divId;
    }

    /**Resolves a division ID to a division.
     *
     * @param divId
     * @return
     */
    public static String getDivision(int divId){
        String div = "";
        try {
            String sql = "SELECT Division FROM client_schedule.first_level_divisions WHERE Division_ID = ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, divId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                div = rs.getString("Division");
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
        System.out.println("Division is " + div);
        return div;
    }

    /**Resolves a division to a country.
     *
     * @param div
     * @return
     */
    public static String getCountry(String div){
        String country = "";
        try {
            String sql = "SELECT Country FROM client_schedule.countries c JOIN client_schedule.first_level_divisions d ON c.Country_ID = d.Country_ID WHERE d.Division = ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setString(1, div);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                country = rs.getString("Country");
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
        System.out.println("Country is " + country);
        return country;
    }

}
