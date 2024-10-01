package test;
import javafx.application.Application;
import javafx.css.converter.InsetsConverter.SequenceConverter;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.*;
import javafx.stage.*;
public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Kien");
        stage.getIcons().add(new Image("file:D:/Images/Game_Image/Setup/General/Icon/0f62924d-5dac-63b1-5a83-e1d3fcd6dd27_1_11zon.png"));
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
    
