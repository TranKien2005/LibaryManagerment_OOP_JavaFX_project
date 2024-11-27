package model;
import org.junit.Test;
import static org.junit.Assert.*;

public class AccountTest {

    @Test
    public void testAccountCreation() {
        Account account = new Account("user1", "password", "User");
        assertNotNull(account);
        assertEquals("user1", account.getUsername());
        assertEquals("password", account.getPassword());
        assertEquals("User", account.getAccountType());
    }

    @Test
    public void testSetUsername() {
        Account account = new Account("user1", "password", "User");
        account.setUsername("newUser");
        assertEquals("newUser", account.getUsername());
    }

    @Test
    public void testSetPassword() {
        Account account = new Account("user1", "password", "User");
        account.setPassword("newPassword");
        assertEquals("newPassword", account.getPassword());
    }

    @Test
    public void testSetAccountType() {
        Account account = new Account("user1", "password", "User");
        account.setAccountType("Admin");
        assertEquals("Admin", account.getAccountType());
    }

    @Test
    public void testGetAccountID() {
        Account account = new Account(1, "user1", "password", "User");
        assertEquals(1, account.getAccountID());
    }

    @Test
    public void testGetUsername() {
        Account account = new Account("user1", "password", "User");
        assertEquals("user1", account.getUsername());
    }

    @Test
    public void testGetPassword() {
        Account account = new Account("user1", "password", "User");
        assertEquals("password", account.getPassword());
    }

    @Test
    public void testGetAccountType() {
        Account account = new Account("user1", "password", "User");
        assertEquals("User", account.getAccountType());
    }
} 