package Controller;

import DAO.AccountDao;
import model.Account;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterControllerTest {

    private registerController registerController;
    private AccountDao accountDao;

    @Before
    public void setUp() {
        registerController = new registerController();
        accountDao = mock(AccountDao.class);
        // Giả lập AccountDao
        when(AccountDao.getInstance()).thenReturn(accountDao);

        // Khởi tạo các trường FXML
        registerController.usernameField = new TextField();
        registerController.passwordField = new PasswordField();
        registerController.confirmPasswordField = new PasswordField();
        registerController.fullnameField = new TextField();
        registerController.emailField = new TextField();
        registerController.phoneField = new TextField();
        registerController.accountTypeComboBox = new ComboBox<>();
    }

    @Test
    public void testHandleRegisterSuccess() {
        registerController.usernameField.setText("newUser");
        registerController.passwordField.setText("password");
        registerController.confirmPasswordField.setText("password");
        registerController.fullnameField.setText("Full Name");
        registerController.emailField.setText("email@example.com");
        registerController.phoneField.setText("1234567890");
        registerController.accountTypeComboBox.setValue("User");

        Account newAccount = new Account("newUser", "password", "User");
        when(accountDao.getByUsername("newUser")).thenReturn(null);
        when(accountDao.insert(newAccount)).thenReturn(true);
        when(accountDao.getID(newAccount)).thenReturn(1);

        registerController.handleRegister();

        verify(accountDao).insert(newAccount);
    }

    @Test
    public void testHandleRegisterUsernameExists() {
        registerController.usernameField.setText("existingUser");
        registerController.passwordField.setText("password");
        registerController.confirmPasswordField.setText("password");
        registerController.fullnameField.setText("Full Name");
        registerController.emailField.setText("email@example.com");
        registerController.phoneField.setText("1234567890");
        registerController.accountTypeComboBox.setValue("User");

        Account existingAccount = new Account("existingUser", "password", "User");
        when(accountDao.getByUsername("existingUser")).thenReturn(existingAccount);

        registerController.handleRegister();

        // Kiểm tra rằng thông báo lỗi được hiển thị
        // (Bạn có thể cần giả lập phương thức util.ErrorDialog.showError)
    }

    // Các phương thức kiểm tra khác...
} 