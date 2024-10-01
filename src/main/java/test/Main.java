package test;
import java.sql.Connection;
import java.sql.DriverManager;

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
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/classicmodels", "root", "");
            System.out.println("Connected to the database!");
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // launch(args);
    }
    }
    
