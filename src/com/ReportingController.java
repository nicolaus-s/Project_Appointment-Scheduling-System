package com.shaffer;

import com.sun.javafx.charts.Legend;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import query.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**Controller for the Reports Page.
 * @author Nick Shaffer
 * @since 1.0
 */
public class ReportingController implements Initializable {
@FXML private ComboBox<Month> comboMonth;
@FXML private ComboBox<String> comboType;
@FXML private ComboBox<String> comboContact;
@FXML private Label resultAppByType;
@FXML private TableView<Appointment> contactSchedule;
@FXML private TableColumn<Appointment, Integer> colApptID;
@FXML private TableColumn<Appointment, String> colApptTitle;
@FXML private TableColumn<Appointment, String> colApptType;
@FXML private TableColumn<Appointment, String> colApptDesc;
@FXML private TableColumn<Appointment, LocalDateTime> colApptStart;
@FXML private TableColumn<Appointment, LocalDateTime> colApptEnd;
@FXML private TableColumn<Appointment, LocalDateTime> colApptCusID;
@FXML private PieChart pieCountry = new PieChart();

    /** Renders all reports when the Reporting page is opened.
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    comboMonth.setItems(TimeHandler.setMonthsList());
    comboType.setItems(AppointmentQuery.setTypeList());
    comboContact.setItems(ContactQuery.setContactNameList());
    pieCountry.setData(CustomerQuery.getCustomerCountryCount());
    }

    /**When a month is selected, results are displayed if a type has already been selected. Otherwise, no results are displayed.
     *
     */
    public void onSelectMonth(){
        filterMonthAndType();
    }

    /**When a type is selected, results are displayed if a month has already been selected. Otherwise, no results are displayed.
     *
     */
    public void onSelectType(){
        filterMonthAndType();
    }

    /**Is called anytime a month or type are selected. If both fields are populated, results are displayed.
     *
     */
    public void filterMonthAndType(){

        ObservableList<Appointment> allAppointments = AppointmentQuery.setAppointmentList();
        FilteredList<Appointment> monthlyTypeAppointments = new FilteredList<>(allAppointments);

        monthlyTypeAppointments.setPredicate(a -> {
            if (!comboMonth.getSelectionModel().isEmpty() && !comboType.getSelectionModel().isEmpty()) {
                Month m = comboMonth.getValue();
                String t = comboType.getValue();
                if(a.StartTime.getMonth().equals(m) && a.Type.equals(t)) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else{
                return false;
            }
        });
        resultAppByType.setText(Integer.toString(monthlyTypeAppointments.size()));
    }

    /**When a Contact is selected, the getContactSchedule() method is called.
     *
     */
    public void onSelectContact(){
        getContactSchedule();
    }

    /**Populates the TableView to display all appointments assigned to the selected contact.
     *
     */
    public void getContactSchedule(){
        ObservableList<Appointment>allAppointments = AppointmentQuery.setAppointmentList();
        FilteredList<Appointment> contactAppointments = new FilteredList<>(allAppointments);

        contactSchedule.setItems(contactAppointments);
        colApptID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colApptTitle.setCellValueFactory(new PropertyValueFactory<>("Title"));
        colApptType.setCellValueFactory(new PropertyValueFactory<>("Type"));
        colApptDesc.setCellValueFactory(new PropertyValueFactory<>("Description"));
        colApptStart.setCellValueFactory(new PropertyValueFactory<>("StartTime"));
        colApptEnd.setCellValueFactory(new PropertyValueFactory<>("EndTime"));
        colApptCusID.setCellValueFactory(new PropertyValueFactory<>("CustomerID"));

        contactAppointments.setPredicate(a -> {
            if(ContactQuery.getContactID(a.Contact) == ContactQuery.getContactID(comboContact.getValue())){
                return true;
            }
            else {
                return false;
            }
        });
    }

    /**Function to lead the user back to the Home Page. Calling this function throughout the NewAppointmentController is preferred so that the code within the function is not repeated multiple times.
     *
     * @param actionEvent
     * @throws IOException
     */
    public void onGoHome(ActionEvent actionEvent) throws IOException {
        pieCountry.getData().clear();
        Parent home_parent = FXMLLoader.load(getClass().getResource("HomePage.fxml"));
        Scene home_scene = new Scene(home_parent);
        Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(home_scene);
        primaryStage.show();
    }
}
