package util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ErrorDialog {
    private static boolean isErrorDialogShowing = false; // Biến cờ để theo dõi trạng thái của thông báo lỗi

    public static void showError(String title, String header, String content, Stage owner) {
        if (!isErrorDialogShowing) {
            isErrorDialogShowing = true;
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle(title);
                alert.setHeaderText(header);
                alert.setContentText(content);
                alert.initModality(Modality.WINDOW_MODAL);
                if (owner != null) {
                    alert.initOwner(owner);
                }
                alert.showAndWait(); // Chờ cho đến khi thông báo được tắt
                isErrorDialogShowing = false; // Đặt lại biến cờ khi thông báo được tắt
            });
        }
    }

    public static void showError(String title, String content, Stage owner) {
        showError(title, null, content, owner);
    }

    public static void showSuccess(String title, String header, String content, Stage owner) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.initModality(Modality.WINDOW_MODAL);
            if (owner != null) {
                alert.initOwner(owner);
            }
            alert.showAndWait(); // Chờ cho đến khi thông báo được tắt
        });
    }

    public static void showSuccess(String title, String content, Stage owner) {
        showSuccess(title, null, content, owner);
    }
}