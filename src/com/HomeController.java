package com.shaffer;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import query.*;

import java.io.IOException;
import java.net.URL;
import java.time.*;
import java.util.*;

/**Controller for the Home Page.
 * @author Nick Shaffer
 * @since 1.0
 */

public class HomeController implements Initializable {
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> colCusID;
    @FXML private TableColumn<Customer, String> colCusName;
    @FXML private TableColumn<Customer, String> colCusAddress;
    @FXML private TableColumn<Customer, String> colCusPostalCode;
    @FXML private TableColumn<Customer, String> colCusPhone;
    @FXML private TableColumn<Customer, Integer> colCusDivID;
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> colApptID;
    @FXML private TableColumn<Appointment, String> colApptTitle;
    @FXML private TableColumn<Appointment, String> colApptDesc;
    @FXML private TableColumn<Appointment, String> colApptLoc;
    @FXML private TableColumn<Appointment, String> colApptContact;
    @FXML private TableColumn<Appointment, String> colApptType;
    @FXML private TableColumn<Appointment, LocalDateTime> colApptStart;
    @FXML private TableColumn<Appointment, LocalDateTime> colApptEnd;
    @FXML private TableColumn<Appointment, Integer> colApptCusID;
    @FXML private TableColumn<Appointment, Integer> colApptUserID;
    @FXML private TableView<Appointment> appointmentTableByMonth;
    @FXML private TableColumn<Appointment, Integer> colApptID1;
    @FXML private TableColumn<Appointment, String> colApptTitle1;
    @FXML private TableColumn<Appointment, String> colApptDesc1;
    @FXML private TableColumn<Appointment, String> colApptLoc1;
    @FXML private TableColumn<Appointment, String> colApptContact1;
    @FXML private TableColumn<Appointment, String> colApptType1;
    @FXML private TableColumn<Appointment, LocalDateTime> colApptStart1;
    @FXML private TableColumn<Appointment, LocalDateTime> colApptEnd1;
    @FXML private TableColumn<Appointment, Integer> colApptCusID1;
    @FXML private TableColumn<Appointment, Integer> colApptUserID1;
    @FXML private TableView<Appointment> appointmentTableByWeek;
    @FXML private TableColumn<Appointment, Integer> colApptID2;
    @FXML private TableColumn<Appointment, String> colApptTitle2;
    @FXML private TableColumn<Appointment, String> colApptDesc2;
    @FXML private TableColumn<Appointment, String> colApptLoc2;
    @FXML private TableColumn<Appointment, String> colApptContact2;
    @FXML private TableColumn<Appointment, String> colApptType2;
    @FXML private TableColumn<Appointment, LocalDateTime> colApptStart2;
    @FXML private TableColumn<Appointment, LocalDateTime> colApptEnd2;
    @FXML private TableColumn<Appointment, Integer> colApptCusID2;
    @FXML private TableColumn<Appointment, Integer> colApptUserID2;

    /**When the user logs into the Home Page, the initialize function populates all customer and appointment tables that are needed.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Homepage Initialized");
        setCustomerTable();
        setAppointmentTable();
        setMonthlyAppointmentTable();
        setWeeklyAppointmentTable();
        System.out.println(LocalDateTime.now());
    }

    /**Opens the New Customer page when clicking the 'Add' button near the Customer table.
     * @param actionEvent
     * @throws IOException
     */
    public void onAddCustomer(ActionEvent actionEvent) throws IOException {
        Parent home_parent = FXMLLoader.load(getClass().getResource("NewCustomer.fxml"));
        Scene home_scene = new Scene(home_parent);
        Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(home_scene);
        primaryStage.show();
    }

    /**Opens the Edit Customer page when clicking the 'Edit' button near the Customer table. Populates the Edit Customer page with the selected customer data.
     * @param actionEvent
     * @throws IOException
     */
    public void onEditCustomer(ActionEvent actionEvent) throws IOException {
        Customer c = customerTable.getSelectionModel().getSelectedItem();
        if(c == null){
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("No customer selected.");
            error.setContentText("Please make a selection from the Customer table, then select 'Update'");
            error.showAndWait();
        }
        else {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("UpdateCustomer.fxml"));
            loader.load();
            UpdateCustomerController controller = loader.getController();
            controller.loadCustomer(c);
            Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            primaryStage.setScene(new Scene(scene));
            primaryStage.show();
        }
    }

    /**When a customer is selected in the Customer table, the customer is deleted when 'Delete' is clicked. This function will inherently delete any appointments for the selected customer.
     * @param actionEvent
     * @throws IOException
     */
    public void onDeleteCustomer(ActionEvent actionEvent){
        try{
        Customer c = customerTable.getSelectionModel().getSelectedItem();
        String id = String.valueOf(c.Id);
        CustomerQuery.delete(id);
        setCustomerTable();
        setAppointmentTable();

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText("Customer Deleted");
        info.setContentText("Customer with id " + id + ", and any pending appointments, have been deleted.");
        info.showAndWait();
        }
        catch(Exception e){
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("No customer selected.");
            error.setContentText("No customer was selected to delete.");
            error.showAndWait();
        }
    }

    /**Opens the New Appointment page when clicking the 'Add' button near the Appointment table.
     * @param actionEvent
     * @throws IOException
     */
    public void onAddAppt(ActionEvent actionEvent) throws IOException{
        Parent home_parent = FXMLLoader.load(getClass().getResource("NewAppointment.fxml"));
        Scene home_scene = new Scene(home_parent);
        Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(home_scene);
        primaryStage.show();
    }

    /**Opens the Edit Appointment page when clicking the 'Edit' button near the Appointment table. Populates the Appointment Customer page with the selected appointment data.
     * @param actionEvent
     * @throws IOException
     */
    public void onEditAppt(ActionEvent actionEvent) throws IOException{
        System.out.println("Edit Appointment Clicked.");
        Appointment a = appointmentTable.getSelectionModel().getSelectedItem();
        if(a == null){
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("No appointment selected.");
            error.setContentText("Please make a selection from the Appointment table, then select 'Update'");
            error.showAndWait();
        }
        else {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("UpdateAppointment.fxml"));
            loader.load();
            UpdateAppointmentController controller = loader.getController();
            controller.loadAppointment(a);
            Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            primaryStage.setScene(new Scene(scene));
            primaryStage.show();
        }
    }

    /**When an appointment is selected in the Appointment table, the appointment is deleted when 'Delete' is clicked.
     * @param actionEvent
     * @throws IOException
     */
    public void onDeleteAppt(ActionEvent actionEvent){
        try{
            Appointment a = appointmentTable.getSelectionModel().getSelectedItem();
            String id = String.valueOf(a.ID);
            AppointmentQuery.delete(id);
            setAppointmentTable();
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setHeaderText("Appointment cancelled.");
            info.setContentText("Appointment with id " + id + " of type " + a.Type + " has been cancelled.");
            info.showAndWait();
            }
         catch(Exception e){
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("No appointment selected.");
            error.setContentText("No appointment was selected to cancel.");
            error.showAndWait();
        }
    }

    /**Populates the Customer table with customers from the database.
     */
    public void setCustomerTable(){
        customerTable.setItems(CustomerQuery.setCustomerList());
        colCusID.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colCusName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        colCusAddress.setCellValueFactory(new PropertyValueFactory<>("Address"));
        colCusPostalCode.setCellValueFactory(new PropertyValueFactory<>("ZipCode"));
        colCusPhone.setCellValueFactory(new PropertyValueFactory<>("PhoneNumber"));
        colCusDivID.setCellValueFactory(new PropertyValueFactory<>("Division"));
    }

    /**Populates the Appointment table with appointments from the database.
     */
    public void setAppointmentTable(){
        appointmentTable.setItems(AppointmentQuery.setAppointmentList());
        colApptID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colApptTitle.setCellValueFactory(new PropertyValueFactory<>("Title"));
        colApptDesc.setCellValueFactory(new PropertyValueFactory<>("Description"));
        colApptLoc.setCellValueFactory(new PropertyValueFactory<>("Location"));
        colApptContact.setCellValueFactory(new PropertyValueFactory<>("Contact"));
        colApptType.setCellValueFactory(new PropertyValueFactory<>("Type"));
        colApptStart.setCellValueFactory(new PropertyValueFactory<>("StartTime"));
        colApptEnd.setCellValueFactory(new PropertyValueFactory<>("EndTime"));
        colApptCusID.setCellValueFactory(new PropertyValueFactory<>("CustomerID"));
        colApptUserID.setCellValueFactory(new PropertyValueFactory<>("UserID"));
    }

    /**Populates the monthly table with all appointments prior to being filtered. The user will not see the pre-filtered table.
     */
    public void setMonthlyAppointmentTable() {
        appointmentTableByMonth.setItems(AppointmentQuery.setAppointmentList());
        colApptID1.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colApptTitle1.setCellValueFactory(new PropertyValueFactory<>("Title"));
        colApptDesc1.setCellValueFactory(new PropertyValueFactory<>("Description"));
        colApptLoc1.setCellValueFactory(new PropertyValueFactory<>("Location"));
        colApptContact1.setCellValueFactory(new PropertyValueFactory<>("Contact"));
        colApptType1.setCellValueFactory(new PropertyValueFactory<>("Type"));
        colApptStart1.setCellValueFactory(new PropertyValueFactory<>("StartTime"));
        colApptEnd1.setCellValueFactory(new PropertyValueFactory<>("EndTime"));
        colApptCusID1.setCellValueFactory(new PropertyValueFactory<>("CustomerID"));
        colApptUserID1.setCellValueFactory(new PropertyValueFactory<>("UserID"));

    }

    /**Populates the weekly table with all appointments prior to being filtered. The user will not see the pre-filtered table.
     */
    public void setWeeklyAppointmentTable(){
        appointmentTableByWeek.setItems(AppointmentQuery.setAppointmentList());
        colApptID2.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colApptTitle2.setCellValueFactory(new PropertyValueFactory<>("Title"));
        colApptDesc2.setCellValueFactory(new PropertyValueFactory<>("Description"));
        colApptLoc2.setCellValueFactory(new PropertyValueFactory<>("Location"));
        colApptContact2.setCellValueFactory(new PropertyValueFactory<>("Contact"));
        colApptType2.setCellValueFactory(new PropertyValueFactory<>("Type"));
        colApptStart2.setCellValueFactory(new PropertyValueFactory<>("StartTime"));
        colApptEnd2.setCellValueFactory(new PropertyValueFactory<>("EndTime"));
        colApptCusID2.setCellValueFactory(new PropertyValueFactory<>("CustomerID"));
        colApptUserID2.setCellValueFactory(new PropertyValueFactory<>("UserID"));
    }

    /**Once the 'This Month' tab is selected on the Appointment table, this function determines which calendar month the local device is in, and filters the table based off of that information. To filter the Appointments down to this month, a lambda expression is used for the predicate. Without the lambda, the predicate takes many more lines and is harder to read and harder to write. Using the lambda expression for the predicate makes the code far more simple and take only one line.
     *
     */
    public void getMonth(){
        Month thisMonth = LocalDate.now().getMonth();
        int thisYear = LocalDate.now().getYear();
        ObservableList<Appointment>allAppointments = AppointmentQuery.setAppointmentList();
        FilteredList<Appointment> monthlyAppointments = new FilteredList<>(allAppointments);

        appointmentTableByMonth.setItems(monthlyAppointments);
        colApptID1.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colApptTitle1.setCellValueFactory(new PropertyValueFactory<>("Title"));
        colApptDesc1.setCellValueFactory(new PropertyValueFactory<>("Description"));
        colApptLoc1.setCellValueFactory(new PropertyValueFactory<>("Location"));
        colApptContact1.setCellValueFactory(new PropertyValueFactory<>("Contact"));
        colApptType1.setCellValueFactory(new PropertyValueFactory<>("Type"));
        colApptStart1.setCellValueFactory(new PropertyValueFactory<>("StartTime"));
        colApptEnd1.setCellValueFactory(new PropertyValueFactory<>("EndTime"));
        colApptCusID1.setCellValueFactory(new PropertyValueFactory<>("CustomerID"));
        colApptUserID1.setCellValueFactory(new PropertyValueFactory<>("UserID"));

        monthlyAppointments.setPredicate(a -> a.StartTime.getYear() == thisYear && a.StartTime.getMonth().equals(thisMonth) && a.StartTime.getYear() == thisYear && a.EndTime.getMonth().equals(thisMonth));

    }

    /**When the 'This Week' tab is clicked, the weekly table determines what day it is based off of the local system date, and presents the user with appointments that are to occur this week between Sunday and Saturday. To filter the Appointments down to this week, a lambda expression is used for the predicate. The lambda expression simplifies the code by condensing the logic into one line. I would add additional lambda expressions throughout the code, but Predicate is the only functional interface used in my code.
     *
     */
    public void getWeek(){
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now();
        switch(today) {
            case SUNDAY:
                from = LocalDate.now();
                to = LocalDate.now().plusDays(6);
                break;
            case MONDAY:
                from = LocalDate.now().minusDays(1);
                to = LocalDate.now().plusDays(5);
                break;
            case TUESDAY:
                from = LocalDate.now().minusDays(2);
                to = LocalDate.now().plusDays(4);
                break;
            case WEDNESDAY:
                from = LocalDate.now().minusDays(3);
                to = LocalDate.now().plusDays(3);
                break;
            case THURSDAY:
                from = LocalDate.now().minusDays(4);
                to = LocalDate.now().plusDays(2);
                break;
            case FRIDAY:
                from = LocalDate.now().minusDays(5);
                to = LocalDate.now().plusDays(1);
                break;
            case SATURDAY:
                from = LocalDate.now().minusDays(6);
                to = LocalDate.now();
                break;
        }

        System.out.println("Today is: " + LocalDate.now().getDayOfWeek());
        System.out.println("Week starts at: " + from);
        System.out.println("Week ends at:" + to);

        ObservableList<Appointment>allAppointments = AppointmentQuery.setAppointmentList();
        FilteredList<Appointment> weeklyAppointments = new FilteredList<>(allAppointments);

        appointmentTableByWeek.setItems(weeklyAppointments);
        colApptID2.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colApptTitle2.setCellValueFactory(new PropertyValueFactory<>("Title"));
        colApptDesc2.setCellValueFactory(new PropertyValueFactory<>("Description"));
        colApptLoc2.setCellValueFactory(new PropertyValueFactory<>("Location"));
        colApptContact2.setCellValueFactory(new PropertyValueFactory<>("Contact"));
        colApptType2.setCellValueFactory(new PropertyValueFactory<>("Type"));
        colApptStart2.setCellValueFactory(new PropertyValueFactory<>("StartTime"));
        colApptEnd2.setCellValueFactory(new PropertyValueFactory<>("EndTime"));
        colApptCusID2.setCellValueFactory(new PropertyValueFactory<>("CustomerID"));
        colApptUserID2.setCellValueFactory(new PropertyValueFactory<>("UserID"));

        LocalDate finalFrom = from;
        LocalDate finalTo = to;

        weeklyAppointments.setPredicate(a -> a.StartTime.toLocalDate().isAfter(finalFrom) && a.EndTime.toLocalDate().isBefore(finalTo));

        }

    /**Leads the user to the Reports page.
      * @param actionEvent
     * @throws IOException
     */
    public void onClickReports(ActionEvent actionEvent) throws IOException{
        Parent home_parent = FXMLLoader.load(getClass().getResource("Reporting.fxml"));
        Scene home_scene = new Scene(home_parent);
        Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(home_scene);
        primaryStage.show();
    }

    /**Logs the current user out; returns the user to the login page to enter credentials again.
     * @param actionEvent
     * @throws IOException
     */
    public void onLogout(ActionEvent actionEvent) throws IOException{
        Parent home_parent = FXMLLoader.load(getClass().getResource("LoginScreen.fxml"));
        Scene home_scene = new Scene(home_parent);
        Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(home_scene);
        primaryStage.show();
    }

    /**When Exit is clicked, the program is closed.
     * @param actionEvent
     */
    public void onExit(ActionEvent actionEvent){
        query.JDBC.closeConnection();
        System.exit(0);
    }

}
