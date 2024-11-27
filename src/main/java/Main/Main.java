package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    private static Main instance;

    public Main() {
        instance = this;
    }

    public static Main getInstance() {
        return instance;
    }
    @Override
    public void start(Stage stage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            if (loader.getLocation() == null) {
            System.err.println("Error: FXML file not found!");
            return;
            }
            Scene scene = new Scene(loader.load());
            stage.setTitle("Đăng nhập");
            stage.setScene(scene);
            stage.setWidth(1000);
            stage.setHeight(600);
            stage.setResizable(false);
            stage.getScene().getRoot().setStyle("-fx-border-color: black; -fx-border-width: 2px;");
            
            // Sử dụng đường dẫn tuyệt đối cho tệp hình ảnh
            Image icon = new Image(getClass().getResourceAsStream("/images/login/logo.png"));
            if (icon.isError()) {
            System.err.println("Error: Image file not found!");
            return;
            }
            stage.getIcons().add(icon);
            
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public static void main(String[] args) {
        launch(args);
        
    }
}