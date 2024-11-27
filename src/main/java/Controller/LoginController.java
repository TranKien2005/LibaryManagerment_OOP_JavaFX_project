package Controller;

import java.io.IOException;
import java.sql.SQLException;

import DAO.AccountDao;
import model.Account;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import util.ErrorDialog;
import util.ThreadManager;
import QR.*;

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
        Account account ;
        try {
            account = accountDao.getByUsername(username);
            if (account == null || !account.getPassword().equals(password)) {
            throw new Exception("Invalid username or password");
            }
             // Show loading stage in the current stage
        Stage currentStage = (Stage) loginButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/loading.fxml"));
      
            Parent root = loader.load();
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle("Loading");
            currentStage.show();

            // Run login process in a new thread using ThreadManager
            util.ThreadManager.execute(() -> {
               
                    // Simulate login processing time
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                Platform.runLater(() -> {
                    try {
                        
                        String fxmlFile = account.getAccountType().equals("User") ? "../view/menuUser.fxml" : "../view/menu.fxml";
                        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource(fxmlFile));
                        
                        int accountId = account.getAccountID();
                        if (account.getAccountType().equals("User")) {
                            menuUserController userLoader = new menuUserController();
                            userLoader.setAccountID(accountId);
                            mainLoader.setController(userLoader);
                        } else {
                            menuController menuLoader = new menuController();
                            menuLoader.setAccountID(accountId);
                            mainLoader.setController(menuLoader);
                        }
                        Parent mainRoot = mainLoader.load();
                        Scene mainScene = new Scene(mainRoot);
                        Stage mainStage = new Stage();
                        mainStage.setScene(mainScene);
                        mainStage.setTitle("Menu");
                        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                        mainStage.setX((screenBounds.getWidth() - 1500) / 2);
                        mainStage.setY((screenBounds.getHeight() - 800) / 2);
                        mainStage.setWidth(1500);
                        mainStage.setHeight(800);
                        mainStage.setResizable(false);
                        // Sử dụng đường dẫn tuyệt đối cho tệp hình ảnh
                        Image icon = new Image(getClass().getResourceAsStream("/images/login/logo.png"));
                        if (icon.isError()) {
                            System.err.println("Error: Image file not found!");
                            return;
                        }
                        mainStage.getIcons().add(icon);

                        currentStage.close();
                        mainStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        ErrorDialog.showError("Error", e.getMessage(), currentStage);
                    }
                });
                
              
            });
       
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            util.ErrorDialog.showError("Database Error", e.getMessage(), null);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            util.ErrorDialog.showError("Login Error", e.getMessage(), null);
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
            util.ErrorDialog.showError("Register Error", e.getMessage(), null);
            e.printStackTrace();
        }
    }

    private void loginByAccountId(int accountId) {
        AccountDao accountDao = AccountDao.getInstance();
        Account account;
        try {
             account = accountDao.get(accountId);
            if (account == null) {
                throw new Exception("Account not found");
            }

            
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/loading.fxml"));
      
            Parent root = loader.load();
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle("Loading");
            currentStage.show();


            // Run login process in a new thread using ThreadManager
            util.ThreadManager.execute(() -> {
               
                    // Simulate login processing time
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                Platform.runLater(() -> {
                    try {
                        
                        String fxmlFile = account.getAccountType().equals("User") ? "../view/menuUser.fxml" : "../view/menu.fxml";
                        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource(fxmlFile));
                        
                        
                        if (account.getAccountType().equals("User")) {
                            menuUserController userLoader = new menuUserController();
                            userLoader.setAccountID(accountId);
                            mainLoader.setController(userLoader);
                        } else {
                            menuController menuLoader = new menuController();
                            menuLoader.setAccountID(accountId);
                            mainLoader.setController(menuLoader);
                        }
                        Parent mainRoot = mainLoader.load();
                        Scene mainScene = new Scene(mainRoot);
                        Stage mainStage = new Stage();
                        mainStage.setScene(mainScene);
                        mainStage.setTitle("Menu");
                        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                        mainStage.setX((screenBounds.getWidth() - 1500) / 2);
                        mainStage.setY((screenBounds.getHeight() - 800) / 2);
                        mainStage.setWidth(1500);
                        mainStage.setHeight(800);
                        mainStage.setResizable(false);
                        // Sử dụng đường dẫn tuyệt đối cho tệp hình ảnh
                        Image icon = new Image(getClass().getResourceAsStream("/images/login/logo.png"));
                        if (icon.isError()) {
                            System.err.println("Error: Image file not found!");
                            return;
                        }
                        mainStage.getIcons().add(icon);

                        currentStage.close();
                        mainStage.show();
                    } catch (IOException e) {
                        throw new RuntimeException("Error loading menu.fxml: " + e.getMessage());
                    }
                
                });
            });
        
        }
        catch (SQLException e) {
            throw new RuntimeException("Database Error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Login Error: " + e.getMessage());
        }
    }

    
    private boolean loginInProgress = false;
    public QRScanner qrScanner;
    private boolean isValidQRCodeFormat(String qrCodeText) {
        if (qrCodeText == null || !qrCodeText.startsWith("accountID:")) {
            return false;
        }
        try {
            Integer.parseInt(qrCodeText.substring("accountID:".length()).trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    @FXML
    private void handleQRLogin() {
    boolean isscanning = (qrScanner != null && qrScanner.isRunning());
    if (isscanning) {
        return;
    }
    qrScanner = QRScanner.getInstance();
    qrScanner.startQRScanner(qrCodeText -> {
        Platform.runLater(() -> {
        
        if (qrCodeText.startsWith("accountID:")) {
            try {
                if (loginInProgress) {
                    
                    return; // Ignore if a login attempt is already in progress
                }
                if (!isValidQRCodeFormat(qrCodeText)) {
                ErrorDialog.showError("QR Code Error", "Invalid QR Code format", null);
                return;
                }
                int accountId = Integer.parseInt(qrCodeText.substring("accountID:".length()).trim());
                loginInProgress = true;
                loginByAccountId(accountId);
                qrScanner.stopQRScanner();
                
            } catch (NumberFormatException e) {
                ErrorDialog.showError("QR Code Error", "Invalid account ID", null);
                e.printStackTrace();
                loginInProgress = false;
            } catch (RuntimeException e) {
                ErrorDialog.showError("QR Code Error", e.getMessage(), null);
                e.printStackTrace();
                loginInProgress = false;
            } catch (Exception e) {
                ErrorDialog.showError("QR Code Error", e.getMessage(), null);
                e.printStackTrace();
                loginInProgress = false;
            }
        } else {
            ErrorDialog.showError("QR Code Error", "Invalid QR Code format", null);
            loginInProgress = false;
        }
        });
    });
    
    }

}