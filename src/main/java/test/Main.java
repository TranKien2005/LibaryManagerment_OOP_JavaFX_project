package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
      if (loader.getLocation() == null) {
          System.err.println("Error: FXML file not found!");
          return;
      }
      Scene scene = new Scene(loader.load());
      stage.setTitle("Đăng nhập");
      stage.setScene(scene);
      stage.setWidth(1000);
      stage.setHeight(600);
      stage.show();
    }
    public static void main(String[] args) {
       launch(args);
    }
    }
    
