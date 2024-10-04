package test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.*;
import javafx.stage.*;

public class Main extends Application {
    private static final String URL = "jdbc:mysql://localhost:3306/librarymanagement";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public void start(Stage stage) throws Exception {
        stage.setTitle("Kien");
        StackPane root = new StackPane();
        root.getChildren().add(new Label("kien"));
        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database!");
        } catch (Exception e) {
            e.printStackTrace();
        }
       Book book1 = new Book("kien","kien","jjkk",2);
       try {
           book1.addBookToDatabase(URL, USER, PASSWORD);
           Book.printAllBooks(URL, USER, PASSWORD);
       } catch (SQLException e) {
           e.printStackTrace();
       }
        launch(args);
    }
}
