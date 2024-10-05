package test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/librarymanagement";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";
    private static Database instance;

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    private Database() {}
    public ObservableList<Document> getAllBooks() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ObservableList<Document> documents = FXCollections.observableArrayList();
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            statement = connection.prepareStatement("SELECT * FROM book");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                documents.add(new Document(
                    resultSet.getString("bookname"),
                    resultSet.getString("author"),
                    resultSet.getString("category"),
                    resultSet.getString("publisher"),
                    resultSet.getInt("year"),
                    resultSet.getInt("quantity")
                ));
            }
            return documents;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
