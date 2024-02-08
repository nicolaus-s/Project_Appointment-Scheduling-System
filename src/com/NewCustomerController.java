package com.shaffer;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import query.Customer;
import query.DivisionQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**Controller for the New Customer Page.
 * @author Nick Shaffer
 * @since 1.0
 */
public class NewCustomerController implements Initializable {
public TextInputControl txtFullName;
public TextInputControl txtAddress;
public TextInputControl txtPostalCode;
public TextInputControl txtPhoneNumber;
public ComboBox<String> comboCountry;
public ComboBox<String> comboDivision;

    /** Presents the New Customer page and populate available countries in a dropdown
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        System.out.println("New customer page initilalized");
        comboCountry.setItems(DivisionQuery.setCountryList());
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

    /**When the 'Add' button is clicked, validates fields to ensure that no fields are left blank. If the submission passes validation, the customer is created and added to the customers tabe.
     *
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void onClickAdd(ActionEvent actionEvent) throws SQLException, IOException {
        String f = txtFullName.getText();
        String a = txtAddress.getText();
        String po = txtPostalCode.getText();
        String ph = txtPhoneNumber.getText();
        String div = comboDivision.getSelectionModel().getSelectedItem();
        if(f.length() > 0 && a.length() > 0 && po.length() > 0 && ph.length() > 0 && div != null) {
            System.out.println("Add clicked");
            Customer newCustomer = new Customer(f, a, po, ph, div);
            System.out.println(newCustomer.getId());
            goHome(actionEvent);
        }
        else{
            {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText("Failed to create customer.");
                error.setContentText("Some or all fields were left blank. Please fill in all fields before clicking 'Submit'.");
                error.showAndWait();
            }
            System.out.println("Fields are blank!");
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
