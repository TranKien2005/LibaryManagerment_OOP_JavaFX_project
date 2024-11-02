package Controller;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    protected TextField usernameField;

    @FXML
    protected PasswordField passwordField;

    @FXML
    protected Button loginButton;
  
    @FXML
    protected void handleLogin() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/menu.fxml"));
        if (loader.getLocation() == null) {
            System.err.println("Error: menu.fxml file not found!");
            return;
        }
        try {
             Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setTitle("Menu"); // Change the title of the stage
        stage.setScene(scene);

        // Get the screen bounds
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - 1600) / 2);
        stage.setY((screenBounds.getHeight() - 900) / 2);
        stage.setWidth(1600);
        stage.setHeight(900);

        stage.show();
        } catch (IOException e) {
            System.err.println("Error loading menu.fxml: " + e.getMessage());
            e.printStackTrace();
            
        }
    }

    @FXML
    private void handleRegister() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/register.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle("Register");
          
            stage.setScene(scene);
            stage.setHeight(650);
            stage.setWidth(1000);
            stage.show();
           
            
        } catch (IOException e) {
            System.err.println("Error loading register.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
}
