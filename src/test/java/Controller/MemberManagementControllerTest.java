package Controller;
import model.User;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MemberManagementControllerTest {

    private MemberManagementController memberManagementController;

    @Before
    public void setUp() {
        memberManagementController = new MemberManagementController();
    }

    @Test
    public void testHandleUpdateUser() {
        User user = new User(1, "John Doe", "john@example.com", "1234567890");
        memberManagementController.getTvMembers().getItems().add(user);
        memberManagementController.getTvMembers().getSelectionModel().select(user);

        memberManagementController.getTfFullname().setText("Jane Doe");
        memberManagementController.handleUpdateUser();

        assertEquals("Jane Doe", user.getFullName());
    }

    @Test
    public void testHandleDeleteUser() {
        User user = new User(1, "John Doe", "john@example.com", "1234567890");
        memberManagementController.getTvMembers().getItems().add(user);
        memberManagementController.getTvMembers().getSelectionModel().select(user);

        memberManagementController.handleDeleteUser();

        assertFalse(memberManagementController.getTvMembers().getItems().contains(user));
    }
}