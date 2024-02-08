package com.shaffer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**Sets the window title and window size, and displays the window showing the LoginPage.
 *
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("LoginScreen.fxml"));
        primaryStage.setTitle("Appointment Scheduler");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();
    }

    /**Opens the MySQL database connection that is used throughout the application.
     *
     * @param args
     */
    public static void main(String[] args) {
        query.JDBC.openConnection();
        System.out.println("DB Connected");
        launch(args);
    }

}
