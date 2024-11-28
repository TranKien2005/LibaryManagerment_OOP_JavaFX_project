package util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Utility class to display error and success dialog boxes in the JavaFX application.
 * Provides static methods to show modal dialog boxes with error or success messages.
 */
public class ErrorDialog {
    private static boolean isErrorDialogShowing = false; // Flag to track the state of the error dialog

    /**
     * Displays an error dialog with the specified title, header, content, and owner stage.
     * Ensures that only one error dialog is shown at a time by using a flag.
     *
     * @param title   the title of the error dialog
     * @param header  the header text of the error dialog
     * @param content the content text of the error dialog
     * @param owner   the owner stage of the dialog, may be null
     */
    public static void showError(String title, String header, String content, Stage owner) {
        // Check if an error dialog is already showing
        if (!isErrorDialogShowing) {
            isErrorDialogShowing = true;
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle(title);
                alert.setHeaderText(header);
                alert.setContentText(content);
                alert.initModality(Modality.WINDOW_MODAL); // Set the modality to block interaction with other windows
                if (owner != null) {
                    alert.initOwner(owner); // Set the owner of the dialog to the provided stage
                }
                alert.showAndWait(); // Wait for the user to close the dialog before proceeding
                isErrorDialogShowing = false; // Reset the flag once the dialog is closed
            });
        }
    }

    /**
     * Displays an error dialog with the specified title and content. The header is left as null.
     *
     * @param title   the title of the error dialog
     * @param content the content text of the error dialog
     * @param owner   the owner stage of the dialog, may be null
     */
    public static void showError(String title, String content, Stage owner) {
        showError(title, null, content, owner); // Call the other showError method with null header
    }

    /**
     * Displays a success dialog with the specified title, header, content, and owner stage.
     *
     * @param title   the title of the success dialog
     * @param header  the header text of the success dialog
     * @param content the content text of the success dialog
     * @param owner   the owner stage of the dialog, may be null
     */
    public static void showSuccess(String title, String header, String content, Stage owner) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.initModality(Modality.WINDOW_MODAL); // Set the modality to block interaction with other windows
            if (owner != null) {
                alert.initOwner(owner); // Set the owner of the dialog to the provided stage
            }
            alert.showAndWait(); // Wait for the user to close the dialog before proceeding
        });
    }

    /**
     * Displays a success dialog with the specified title and content. The header is left as null.
     *
     * @param title   the title of the success dialog
     * @param content the content text of the success dialog
     * @param owner   the owner stage of the dialog, may be null
     */
    public static void showSuccess(String title, String content, Stage owner) {
        showSuccess(title, null, content, owner); // Call the other showSuccess method with null header
    }
}
