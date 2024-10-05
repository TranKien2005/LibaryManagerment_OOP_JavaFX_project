package test;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;
  
    @FXML
    private void handleLogin() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
        if (loader.getLocation() == null) {
            System.err.println("Error: menu.fxml file not found!");
            return;
        }
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading menu.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
