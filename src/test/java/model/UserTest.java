package model;
import org.junit.Test;
import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void testUserCreation() {
        User user = new User(1, "John Doe", "john@example.com", "1234567890");
        assertNotNull(user);
        assertEquals("John Doe", user.getFullName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("1234567890", user.getPhone());
    }

    @Test
    public void testSetFullName() {
        User user = new User(1, "John Doe", "john@example.com", "1234567890");
        user.setFullName("Jane Doe");
        assertEquals("Jane Doe", user.getFullName());
    }

    @Test
    public void testSetEmail() {
        User user = new User(1, "John Doe", "john@example.com", "1234567890");
        user.setEmail("jane@example.com");
        assertEquals("jane@example.com", user.getEmail());
    }

    @Test
    public void testSetPhone() {
        User user = new User(1, "John Doe", "john@example.com", "1234567890");
        user.setPhone("0987654321");
        assertEquals("0987654321", user.getPhone());
    }

    @Test
    public void testGetAccountID() {
        User user = new User(1, "John Doe", "john@example.com", "1234567890");
        assertEquals(1, user.getAccountID());
    }

    @Test
    public void testSetAccountID() {
        User user = new User(1, "John Doe", "john@example.com", "1234567890");
        user.setAccountID(2);
        assertEquals(2, user.getAccountID());
    }

    @Test
    public void testUserFullName() {
        User user = new User(1, "John Doe", "john@example.com", "1234567890");
        assertEquals("John Doe", user.getFullName());
    }

    @Test
    public void testUserEmail() {
        User user = new User(1, "John Doe", "john@example.com", "1234567890");
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    public void testUserPhone() {
        User user = new User(1, "John Doe", "john@example.com", "1234567890");
        assertEquals("1234567890", user.getPhone());
    }
} 