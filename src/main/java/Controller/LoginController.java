package Controller;

import java.io.IOException;

import DAO.AccountDao;
import model.Account;
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
import javafx.scene.image.Image;
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
        String username = usernameField.getText();
        String password = passwordField.getText();

        AccountDao accountDao = AccountDao.getInstance();
        Account account = accountDao.getByUsername(username);

        // Show loading stage in the current stage
        Stage currentStage = (Stage) loginButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/loading.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle("Loading");
            currentStage.show();

            // Run login process in a new thread
            new Thread(() -> {
                try {
                    // Simulate login processing time
                    Thread.sleep(1000);

                    // After processing, load the main application stage
                    Platform.runLater(() -> {
                        try {
                            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("../view/menu.fxml"));
                            Parent mainRoot = mainLoader.load();
                            Scene mainScene = new Scene(mainRoot);
                            Stage mainStage = new Stage();
                            mainStage.setScene(mainScene);
                            mainStage.setTitle("Menu");
                            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                            mainStage.setX((screenBounds.getWidth() - 1600) / 2);
                            mainStage.setY((screenBounds.getHeight() - 900) / 2);
                            mainStage.setWidth(1600);
                            mainStage.setHeight(900);
                            mainStage.setResizable(false);
                             // Sử dụng đường dẫn tuyệt đối cho tệp hình ảnh
                            Image icon = new Image(getClass().getResourceAsStream("/images/login/logo.png"));
                            if (icon.isError()) {
                            System.err.println("Error: Image file not found!");
                            return;
                            }
                            mainStage.getIcons().add(icon);
                            currentStage.close();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mainStage.show();
                            
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
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