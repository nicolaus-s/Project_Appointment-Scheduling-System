package com.shaffer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import query.Customer;
import query.CustomerQuery;
import query.DivisionQuery;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**Controller for the Update Customer Page.
 * @author Nick Shaffer
 * @since 1.0
 */
public class UpdateCustomerController implements Initializable {
    @FXML
    private TextInputControl txtID;
    @FXML
    private TextInputControl txtFullName;
    @FXML
    private TextInputControl txtAddress;
    @FXML
    private TextInputControl txtPostalCode;
    @FXML
    private TextInputControl txtPhoneNumber;
    @FXML
    private ComboBox<String> comboCountry;
    @FXML
    private ComboBox<String> comboDivision;

    /**Opens the Update Customer page with all data prefilled.
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        System.out.println("Update customer page initilalized");
        comboCountry.setItems(DivisionQuery.setCountryList());

    }

    /**Populates all fields on the form with the Customer data.
     *
     * @param c
     */
    public void loadCustomer(Customer c){
        String country = DivisionQuery.getCountry(c.Division);
        txtID.setText(String.valueOf(c.Id));
        txtFullName.setText(c.Name);
        txtAddress.setText(c.Address);
        txtPostalCode.setText(c.ZipCode);
        txtPhoneNumber.setText(c.PhoneNumber);
        comboCountry.setValue(country);
        comboDivision.setItems(DivisionQuery.setDivisonList(country));
        comboDivision.setValue(c.Division);

    }

    /** When a country is selected in the country dropdown, the dropdown foe divisions (i.e. states, provinces) is populated.
     *
     * @param actionEvent
     */
    public void selectCountry(ActionEvent actionEvent){
        String country = comboCountry.getSelectionModel().getSelectedItem();
        System.out.println(country);
        comboDivision.setItems(DivisionQuery.setDivisonList(country));
    }

    /**All fields are validated to ensure that no fields are empty. Failing to update the customer will present the user with a relevant error message. Otherwise, the customer is updated and the user is led back to the Home Page.
     *
     * @param actionEvent
     * @throws SQLException
     */
    public void onClickUpdate(ActionEvent actionEvent) throws SQLException, IOException {
        String id = txtID.getText();
        String f = txtFullName.getText();
        String a = txtAddress.getText();
        String po = txtPostalCode.getText();
        String ph = txtPhoneNumber.getText();
        String div = comboDivision.getSelectionModel().getSelectedItem();
        if(f.length() > 0 && a.length() > 0 && po.length() > 0 && ph.length() > 0 && div != null) {
            System.out.println("Update clicked");
            CustomerQuery.update(id, f, a, po, ph, div);
            goHome(actionEvent);
        }
        else{
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Failed to update customer.");
            error.setContentText("Some or all fields were left blank. Please fill in all fields before clicking 'Submit'.");
            error.showAndWait();
        }
    }

    /**If Cancel is clicked, no data is saved and the user is led back to the Home Page.
     *
     * @param actionEvent
     * @throws IOException
     */
    public void onClickCancel(ActionEvent actionEvent) throws IOException {
        goHome(actionEvent);
    }

    /**Function to lead the user back to the Home Page. Calling this function throughout the NewAppointmentController is preferred so that the code within the function is not repeated multiple times.
     *
     * @param actionEvent
     * @throws IOException
     */
    public void goHome(ActionEvent actionEvent) throws IOException {
        Parent home_parent = FXMLLoader.load(getClass().getResource("HomePage.fxml"));
        Scene home_scene = new Scene(home_parent);
        Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(home_scene);
        primaryStage.show();
    }
}
