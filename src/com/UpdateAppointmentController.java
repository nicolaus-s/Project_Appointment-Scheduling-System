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

/**Controller for the Update Appointment Page.
 * @author Nick Shaffer
 * @since 1.0
 */
public class UpdateAppointmentController implements Initializable {
    public TextInputControl txtID;
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

    /**Opens the Update Appointment page with all data prefilled.
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

    /**Populates all fields on the form with the Appointment data.
     *
     * @param a
     */
    public void loadAppointment(Appointment a){
        txtID.setText(String.valueOf(a.ID));
        txtTitle.setText(a.Title);
        txtDesc.setText(a.Description);
        txtLocation.setText(a.Location);
        txtType.setText(a.Type);
        dateStart.setValue(a.StartTime.toLocalDate());
        comboSH.setValue(a.StartTime.getHour());
        comboSM.setValue(a.StartTime.getMinute());
        dateEnd.setValue(a.EndTime.toLocalDate());
        comboEH.setValue(a.EndTime.getHour());
        comboEM.setValue(a.EndTime.getMinute());
        comboContact.setValue(a.Contact);
        comboCustomer.setValue(CustomerQuery.getCustomerByID(a.CustomerID));
        comboUser.setValue(UserQuery.getUserName(a.UserID));
    }

    /**All fields are validated to ensure that no fields are empty, and that the selected appointment start/end times are within business hours. Failing to update the appointment will present the user with a relevant error message. Otherwise, the appointment is updated and the user is led back to the Home Page.
     *
     * @param actionEvent
     * @throws SQLException
     */
    public void onSubmit(ActionEvent actionEvent) throws SQLException {
        String id = txtID.getText();
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
                boolean validDate = query.TimeHandler.isBusinessHours(startDate, endDate);
                System.out.println("Business hours validation: " + validDate);

                boolean conflict = TimeHandler.checkConflictOnEdit(cusID, id, utcStart, utcEnd);

                if(conflict){
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setHeaderText("Failed to create appointment.");
                    error.setContentText("This customer has a conflicting appointment at the selected time.");
                    error.showAndWait();
                    return;
                }

                if(validDate){
                    System.out.println("ID: " + id + "\n" + "Title: " + t + "\n" + "Description: " + d + "\n" + "Location: " + l + "\n" + "Type: " + ty + "\n" + "Start: " + startDate + "\n" + "End: " + endDate + "\n" + "Contact: " + conID + "\n" + "Customer: " + cusID + "\n" + "Current User: " + user);
                    AppointmentQuery.update(id, t, d, l, ty, utcStart, utcEnd, cusID, user, conID);
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
