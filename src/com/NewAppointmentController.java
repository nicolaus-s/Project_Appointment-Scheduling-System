package com.shaffer;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import query.*;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.ResourceBundle;

/**Controller for the New Appointment Page.
 * @author Nick Shaffer
 * @since 1.0
 */
public class NewAppointmentController implements Initializable {
    public TextInputControl txtTitle;
    public TextInputControl txtDesc;
    public TextInputControl txtLocation;
    public TextInputControl txtType;
    public DatePicker dateStart;
    public ComboBox<Integer> comboSH;
    public ComboBox<Integer> comboSM;
    public DatePicker dateEnd;
    public ComboBox<Integer> comboEH;
    public ComboBox<Integer> comboEM;
    public ComboBox<String> comboContact;
    public ComboBox<String> comboCustomer;
    public ComboBox<String> comboUser;

    /** New Appointment page is rendered with combo boxes containing all customer names, contact names, hours, minutes, and stores the current user in a variable for later user.
     *
     * @param url
     * @param resourceBundle
     */
    public void initialize(URL url, ResourceBundle resourceBundle){
        comboContact.setItems(ContactQuery.setContactNameList());
        comboCustomer.setItems(CustomerQuery.setCustomerNameList());
        comboUser.setItems(UserQuery.getAllUsers());
        ObservableList<Integer> hours = TimeHandler.setHoursList();
        ObservableList<Integer> minutes = TimeHandler.setMinutesList();
        comboSH.setItems(hours);
        comboSM.setItems(minutes);
        comboEH.setItems(hours);
        comboEM.setItems(minutes);

    }

    /** All fields are validated to ensure that no fields are empty, and that the selected appointment start/end times are within business hours. Failing to create the appointment will present the user with a relevant error message. Otherwise, the user is led back to the Home Page.
     *
     * @param actionEvent
     * @throws SQLException
     */
    public void onClickAdd(ActionEvent actionEvent) throws SQLException {
        String t = txtTitle.getText();
        String d = txtDesc.getText();
        String l = txtLocation.getText();
        String ty = txtType.getText();
        int user = UserQuery.getUserID(comboUser.getValue());
        System.out.println("Current user id: " + user);
        int cusID = CustomerQuery.getCustomerID(comboCustomer.getValue());
        System.out.println("Customer id: " + cusID);
        int conID = ContactQuery.getContactID(comboContact.getValue());
        System.out.println("Contact id: " + conID);
        boolean valid = t.length() > 0 && d.length() > 0 && l.length() > 0 && ty.length() > 0 && conID > 0 && cusID > 0 && user > 0;
        //Field Validation
        System.out.println(valid);
        if (valid){
            //Date transformation and validation
            try {
                //Retrieve dates and times from form
                LocalDate s = dateStart.getValue();
                int sh = comboSH.getSelectionModel().getSelectedItem();
                int sm = comboSM.getSelectionModel().getSelectedItem();
                LocalDate e = dateEnd.getValue();
                int eh = comboEH.getSelectionModel().getSelectedItem();
                int em = comboEM.getSelectionModel().getSelectedItem();
                //Build date time values
                ZonedDateTime startDate = TimeHandler.buildLocalDate(s, sh, sm);
                ZonedDateTime endDate = TimeHandler.buildLocalDate(e, eh, em);
                LocalDateTime utcStart = TimeHandler.getUTCDate(startDate);
                LocalDateTime utcEnd = TimeHandler.getUTCDate(endDate);

                if(startDate.equals(endDate)){
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setHeaderText("Failed to create appointment.");
                    error.setContentText("The appointment start time and end time can't be the same time.");
                    error.showAndWait();
                    return;
                }

                //Check for conflicting appointments
                if(TimeHandler.checkConflict(cusID, utcStart, utcEnd)){
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setHeaderText("Failed to create appointment.");
                    error.setContentText("This customer has a conflicting appointment at the selected time.");
                    error.showAndWait();
                    return;
                }

                //Check if within business hours
                if(query.TimeHandler.isBusinessHours(startDate, endDate)){
                    AppointmentQuery.insert(t, d, l, ty, utcStart, utcEnd, cusID, user, conID);
                    goHome(actionEvent);
                }


            }
            //Dates are blank error
            catch (Exception ex){
                System.out.println(ex);
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText("Failed to create appointment.");
                error.setContentText("Please set the start and end date/times for the appointment.");
                error.showAndWait();
            }
        }
        //Fields are blank error
        else {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Failed to create appointment.");
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
