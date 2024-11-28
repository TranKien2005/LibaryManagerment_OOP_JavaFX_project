package Controller;

import java.sql.SQLException;
import java.util.List;

import DAO.AccountDao;
import DAO.UserDao;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Account;
import model.User;
import util.ThreadManager;

/**
 * Controller for managing user members, including viewing, updating, filtering, and deleting users.
 */
public class MemberManagementController {

    @FXML
    private TextField tfMemberFilter;
    @FXML
    private TableView<User> tvMembers;
    @FXML
    private VBox rootVBox;
    @FXML
    private TableColumn<User, Integer> colUserId;
    @FXML
    private TableColumn<User, String> colUsername;
    @FXML
    private TableColumn<User, String> colPhone;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private TextField tfFullname;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfPhone;

    private final ObservableList<User> userList = FXCollections.observableArrayList();

    /**
     * Initializes the controller, setting up the table view and loading users into the table.
     */
    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            initializeTableView();
            loadUsers();
        });

        tvMembers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    tfUsername.setText(newValue.getFullName());
                    Account account = AccountDao.getInstance().get(newValue.getAccountID());
                    pfPassword.setText(account.getPassword());
                    tfUsername.setText(account.getUsername());
                    tfFullname.setText(newValue.getFullName());
                    tfEmail.setText(newValue.getEmail());
                    tfPhone.setText(newValue.getPhone());
                } catch (SQLException e) {
                    util.ErrorDialog.showError("Database Error", e.getMessage(),
                            (Stage) rootVBox.getScene().getWindow());
                }
            }
        });
    }

    /**
     * Initializes the columns of the table view to display user information.
     */
    private void initializeTableView() {
        colUserId.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getAccountID()));
        colUsername.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));
        colPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        tvMembers.setItems(userList);
    }

    /**
     * Loads all users from the database and updates the table view.
     */
    private void loadUsers() {
        try {
            List<User> users = UserDao.getInstance().getAll();
            Platform.runLater(() -> {
                userList.clear();
                userList.addAll(users);
            });
        } catch (SQLException e) {
            Platform.runLater(() -> util.ErrorDialog.showError("Database Error", e.getMessage(),
                    (Stage) rootVBox.getScene().getWindow()));
        } catch (Exception e) {
            Platform.runLater(
                    () -> util.ErrorDialog.showError("Error", e.getMessage(), (Stage) rootVBox.getScene().getWindow()));
        }
    }

    /**
     * Handles filtering the user list based on the text input in the filter field.
     */
    @FXML
    private void handleMemberFilterAction() {
        String filterText = tfMemberFilter.getText().toLowerCase().trim();

        ThreadManager.execute(() -> {
            ObservableList<User> filteredList;
            if (filterText.isEmpty()) {
                filteredList = FXCollections.observableArrayList(userList);
            } else {
                filteredList = FXCollections.observableArrayList(
                        userList.filtered(user -> user.getFullName().toLowerCase().contains(filterText) ||
                                user.getPhone().toLowerCase().contains(filterText) ||
                                user.getEmail().toLowerCase().contains(filterText)));
            }

            Platform.runLater(() -> tvMembers.setItems(filteredList));
        });
    }

    /**
     * Handles updating the details of a selected user.
     * Validates input fields and updates the user in the database.
     */
    @FXML
    private void handleUpdateUser() {
        User selectedUser = tvMembers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            // Show an alert that no user is selected
            util.ErrorDialog.showError("No User Selected", "Please select a user to update.",
                    (Stage) rootVBox.getScene().getWindow());
            return;
        }

        // Validate input fields
        if (!validateInputFields()) {
            return;
        }

        // Update user object with new values
        selectedUser.setFullName(tfFullname.getText());
        selectedUser.setPhone(tfPhone.getText());
        selectedUser.setEmail(tfEmail.getText());
        int userId = tvMembers.getSelectionModel().getSelectedItem().getAccountID();

        try {
            UserDao.getInstance().update(selectedUser, userId);
            AccountDao.getInstance().updatePassword(userId, pfPassword.getText());
            util.ErrorDialog.showSuccess("Update Successful", "User details have been successfully updated.",
                    (Stage) rootVBox.getScene().getWindow());
        } catch (SQLException e) {
            util.ErrorDialog.showError("Database Error", e.getMessage(), (Stage) rootVBox.getScene().getWindow());
        } catch (Exception e) {
            util.ErrorDialog.showError("Error", e.getMessage(), (Stage) rootVBox.getScene().getWindow());
        }

        // Refresh table view
        ThreadManager.execute(() -> {
            Platform.runLater(() -> {
                refreshTableView();
            });
        });
    }

    /**
     * Validates the input fields for updating user details.
     * 
     * @return True if all fields are valid, false otherwise.
     */
    private boolean validateInputFields() {
        // Perform validation on input fields
        if (tfUsername.getText().isEmpty() || tfPhone.getText().isEmpty() || tfEmail.getText().isEmpty()) {
            util.ErrorDialog.showError("Invalid Input", "Please fill in all fields.",
                    (Stage) rootVBox.getScene().getWindow());
            return false;
        }

        return true;
    }

    /**
     * Refreshes the table view by reloading all user data from the database.
     */
    private void refreshTableView() {
        try {
            userList.setAll(UserDao.getInstance().getAll());
        } catch (SQLException e) {
            util.ErrorDialog.showError("Database Error", e.getMessage(), (Stage) rootVBox.getScene().getWindow());
        } catch (Exception e) {
            util.ErrorDialog.showError("Error", e.getMessage(), (Stage) rootVBox.getScene().getWindow());
        }
        tfUsername.clear();
        pfPassword.clear();
        tfFullname.clear();
        tfEmail.clear();
        tfPhone.clear();
    }

    /**
     * Handles the deletion of a selected user after user confirmation.
     */
    @FXML
    private void handleDeleteUser() {
        User selectedUser = tvMembers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            util.ErrorDialog.showError("No User Selected", "Please select a user to delete.",
                    (Stage) rootVBox.getScene().getWindow());
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete the user: " + selectedUser.getFullName() + "?", ButtonType.OK,
                ButtonType.CANCEL);
        confirmAlert.initOwner(rootVBox.getScene().getWindow());

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Perform deletion
                ThreadManager.execute(() -> {
                    try {
                        UserDao.getInstance().delete(selectedUser.getAccountID());
                        Platform.runLater(() -> {
                            userList.remove(selectedUser);
                            refreshTableView();
                        });
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Platform.runLater(() -> util.ErrorDialog.showError("Database Error", e.getMessage(),
                                (Stage) rootVBox.getScene().getWindow()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> util.ErrorDialog.showError("Error", e.getMessage(),
                                (Stage) rootVBox.getScene().getWindow()));
                    }
                });
            }
        });
    }

    /**
     * Displays the user's password in a dialog box.
     */
    @FXML
    private void handleShowPassword() {
        util.ErrorDialog.showError("Password", pfPassword.getText(), (Stage) rootVBox.getScene().getWindow());
    }

    /**
     * Handles refreshing the user list and table view.
     */
    @FXML
    private void handleRefresh() {
        refreshTableView();
        loadUsers();
    }

    /**
     * Reloads the user list and refreshes the table view.
     */
    public void reload() {
        refreshTableView();
        loadUsers();
    }
}
