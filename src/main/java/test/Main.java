package test;
import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.*;
import javafx.stage.*;
public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Kien");
        StackPane root = new StackPane();
        root.getChildren().add(new Label("kien"));
        Scene scene = new Scene(root,400,400);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
    }
    
