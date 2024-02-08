package query;

import java.sql.SQLException;

public class Customer {
    public int Id;
    public String Name;
    public String Address;
    public String ZipCode;
    public String PhoneNumber;
    public String Division;

    /**Represents a new customer to be added to the database.
     *
     * @param name
     * @param address
     * @param zipCode
     * @param phoneNumber
     * @param division
     * @throws SQLException
     */
    public Customer(String name, String address, String zipCode, String phoneNumber, String division) throws SQLException {
        Id = CustomerQuery.insert(name, address, zipCode, phoneNumber, division);
        Name = name;
        Address = address;
        ZipCode = zipCode;
        PhoneNumber = phoneNumber;
        Division = division;

    }

    /**Represents an existing customer in the database.
     *
     * @param id
     * @param name
     * @param address
     * @param zipCode
     * @param phoneNumber
     * @param division
     * @throws SQLException
     */
    public Customer(int id, String name, String address, String zipCode, String phoneNumber, String division) throws SQLException {
        Id = id;
        Name = name;
        Address = address;
        ZipCode = zipCode;
        PhoneNumber = phoneNumber;
        Division = division;
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getZipCode() {
        return ZipCode;
    }

    public void setZipCode(String zipCode) {
        ZipCode = zipCode;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getDivision() {
        return Division;
    }

    public void setDivision(String division) {
        Division = division;
    }
}
