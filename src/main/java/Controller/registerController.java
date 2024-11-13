package Controller;

import DAO.AccountDao;
import DAO.UserDao;
import DAO.ManagerDao;
import model.Account;
import model.User;
import model.Manager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class registerController {

    @FXML
    protected TextField usernameField;

    @FXML
    protected PasswordField passwordField;

    @FXML
    protected PasswordField confirmPasswordField;

   
    @FXML
    protected TextField emailField;

    @FXML
    protected TextField phoneField;

    @FXML
    protected ComboBox<String> accountTypeComboBox;

    @FXML
    protected Button registerButton;

    @FXML
    protected void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
       
        String email = emailField.getText();
        String phone = phoneField.getText();
        String accountType = accountTypeComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()  || email.isEmpty() || phone.isEmpty() || accountType == null) {
            showErrorAlert("Registration Failed", "All fields must be filled out.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showErrorAlert("Registration Failed", "Passwords do not match.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showErrorAlert("Registration Failed", "Invalid email format.");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            showErrorAlert("Registration Failed", "Invalid phone number format. It should be 10 digits.");
            return;
        }

        AccountDao accountDao = AccountDao.getInstance();
        Account existingAccount = accountDao.getByUsername(username);

        if (existingAccount != null) {
            showErrorAlert("Registration Failed", "Username already exists.");
            return;
        }

        Account newAccount = new Account(username, password, accountType);
        accountDao.insert(newAccount);

        
        int accountId = accountDao.getID(newAccount);

        // Cập nhật thông tin người dùng hoặc quản lý
        if (accountType.equals("User")) {
            UserDao userDao = UserDao.getInstance();
            User newUser = new User(accountId, username, email, phone);
            userDao.update(newUser, accountId);
        } else if (accountType.equals("Manager")) {
            ManagerDao managerDao = ManagerDao.getInstance();
            Manager newManager = new Manager(accountId, username, email, phone);
            managerDao.update(newManager, accountId);
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Registration Successful");
        alert.setHeaderText(null);
        alert.setContentText("Account created successfully. You can now log in.");
        alert.showAndWait();

        // Chuyển đổi sang màn hình đăng nhập
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setTitle("Đăng nhập");
            stage.getScene().setRoot(loginRoot);
            stage.setWidth(1000);
            stage.setHeight(600);
            stage.centerOnScreen();
            stage.setMaximized(false); // Set the stage to full screen
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Lỗi không xác định", "Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    @FXML
    protected void handleLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setTitle("Đăng nhập");
            stage.getScene().setRoot(loginRoot);
            stage.setWidth(1000);
            stage.setHeight(600);
            stage.centerOnScreen();
            stage.setMaximized(false);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Lỗi không xác định", "Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}