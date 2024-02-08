package com.shaffer;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import query.Appointment;
import query.AppointmentQuery;
import query.TimeHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

/**Controller for the Login Page.
 * @author Nick Shaffer
 * @since 1.0
 */
public class LoginController implements Initializable {
    public Label labelError;
    public Label labelLocale;
    public Label titleUnField;
    public Label titlePwField;
    public TextInputControl txtUsername;
    public TextInputControl txtPassword;
    public String errorText;

    /**Upon starting the program, the Login page is the first to be displayed, requiring a username and password
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelLocale.setText(ZoneId.systemDefault().toString());
        Locale locale = Locale.getDefault();
        setLoginLanguage(locale);
    }

    /**Presents a notification if there are any appointments that start within 15 minutes of a successful login. Otherwise, alerts that there are no upcoming appointments.
      */
    public void appointmentAlert(){
        ObservableList<Appointment> upcoming = AppointmentQuery.loginAppointmentCheck();
        if(upcoming.isEmpty()){
            Alert alert= new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Upcoming appointments");
            alert.setContentText("There are no appointments within the next 15 minutes.");
            alert.showAndWait();
        }
        else{
            String appointments = "";

            for(int i = 0; i < upcoming.size(); i++){
               appointments = appointments + "Appointment ID " + upcoming.get(i).ID + " starts at " + upcoming.get(i).StartTime + '\n';
              }

            Alert alert= new Alert(Alert.AlertType.INFORMATION);
            upcoming.forEach( (a) -> System.out.println(a.ID));
            alert.setHeaderText("Upcoming appointments");
            alert.setContentText("The following appointments start within the next 15 minutes: " + '\n' + appointments);
            alert.showAndWait();
        }
    }

    /**Sets the Login page language to be English or French. The language applies to field titles and login error messages.
     *
     * @param lcl
     */
    public void setLoginLanguage(Locale lcl) {
        String lang = lcl.getLanguage();
        System.out.println("Language: " + lang);
        if (labelLocale != null) {
            if (lang.equals("en")) {
                titleUnField.setText("Username");
                titlePwField.setText("Password");
                errorText = "The username does not exist or the password is incorrect. Please try again.";
            } else if (lang.equals("fr")) {
                titleUnField.setText("Nom d'utilisateur");
                titlePwField.setText("Mot de passe");
                errorText = "Le nom d'utilisateur n'existe pas ou le mot de passe est incorrect. Veuillez réessayer.";
            } else {
                labelLocale.setText("Unsupported Language");
                titleUnField.setText("Username");
                titlePwField.setText("Password");
                errorText = "The username does not exist or the password is incorrect. Please try again.";
            }
        }
    }

    /**Records the login attempt to login_activity.txt, regardless if the attempt was successful or a failure. For each entry, the current date/time, user name, and outcome are recorded.
     *
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void onLogin(ActionEvent actionEvent) throws SQLException, IOException {
        LocalDateTime now = LocalDateTime.now();
        Timestamp utcNow = TimeHandler.getUTCDate(now);
        File file = new File("login_activity.txt");
        FileWriter fileWrite = new FileWriter(file, true);
        String u = txtUsername.getText();
        String p = txtPassword.getText();
        int uid = query.UserQuery.select(u, p);
        if (uid > 0) {
            fileWrite.write("\n" + utcNow + " - " + u + " - " + "SUCCESS");
            fileWrite.close();
            Parent home_parent = FXMLLoader.load(getClass().getResource("HomePage.fxml"));
            Scene home_scene = new Scene(home_parent);
            Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            primaryStage.setScene(home_scene);
            primaryStage.show();
            appointmentAlert();

        } else {
            fileWrite.write("\n" + utcNow + " - " + u + " - " + "FAILURE");
            fileWrite.close();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Login Failure");
            error.setContentText(errorText);
            error.showAndWait();
        }
    }

}