package Controller;

import java.io.IOException;
import java.sql.SQLException;

import DAO.AccountDao;
import DAO.UserDao;
import DAO.ManagerDao;
import model.Account;
import model.User;
import model.Manager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    protected TextField fullnameField;

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
        String fullName = fullnameField.getText();

        String email = emailField.getText();
        String phone = phoneField.getText();
        String accountType = accountTypeComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty() || phone.isEmpty()
                || accountType == null) {
            util.ErrorDialog.showError("Registration Failed", "All fields must be filled out.",
                    (Stage) registerButton.getScene().getWindow());
            return;
        }

        if (!password.equals(confirmPassword)) {
            util.ErrorDialog.showError("Registration Failed", "Passwords do not match.",
                    (Stage) registerButton.getScene().getWindow());
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            util.ErrorDialog.showError("Registration Failed", "Invalid email format.",
                    (Stage) registerButton.getScene().getWindow());
            return;
        }

        try {
            AccountDao accountDao = AccountDao.getInstance();
            Account existingAccount = accountDao.getByUsername(username);

            if (existingAccount != null) {
                util.ErrorDialog.showError("Registration Failed", "Username already exists.",
                        (Stage) registerButton.getScene().getWindow());
                return;
            }

            Account newAccount = new Account(username, password, accountType);
            accountDao.insert(newAccount);

            int accountId = accountDao.getID(newAccount);

            // Cập nhật thông tin người dùng hoặc quản lý
            if (accountType.equals("User")) {
                UserDao userDao = UserDao.getInstance();
                User newUser = new User(accountId, fullName, email, phone);
                userDao.update(newUser, accountId);
            } else if (accountType.equals("Manager")) {
                ManagerDao managerDao = ManagerDao.getInstance();
                Manager newManager = new Manager(accountId, fullName, email, phone);
                managerDao.update(newManager, accountId);
            }

            util.ErrorDialog.showSuccess("Registration Successful", "Account created successfully. You can now log in.",
                    (Stage) registerButton.getScene().getWindow());
        } catch (SQLException e) {
            util.ErrorDialog.showError("Database Error", e.getMessage(), (Stage) registerButton.getScene().getWindow());
        } catch (IllegalArgumentException e) {
            util.ErrorDialog.showError("Registration Failed", e.getMessage(),
                    (Stage) registerButton.getScene().getWindow());
        }

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
        } catch (IOException e) {
            e.printStackTrace();
            util.ErrorDialog.showError("Lỗi không xác định", e.getMessage(),
                    (Stage) registerButton.getScene().getWindow());
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
            util.ErrorDialog.showError("Lỗi không xác định", e.getMessage(),
                    (Stage) registerButton.getScene().getWindow());
        }
    }

}