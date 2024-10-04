package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Book {
    private int id;
    private String title;
    private String author;
    private String category;
    private int availableCopies;

    // Constructor
    public Book(String title, String author, String category, int availableCopies) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.availableCopies = availableCopies;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    // Method to display book information
    public void displayInfo() {
        System.out.println("ID: " + id);
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Category: " + category);
        System.out.println("Available Copies: " + availableCopies);
    }

    // Method to add a new book to the database
    public void addBookToDatabase(String url, String user, String password) throws SQLException {
        String query = "INSERT INTO Document (title, author, category, availableCopies) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, author);
            preparedStatement.setString(3, category);
            preparedStatement.setInt(4, availableCopies);
            preparedStatement.executeUpdate();

            // Retrieve the generated ID
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.id = generatedKeys.getInt(1);
                }
            }
        }
    }

    // Static method to print all books from the database
    public static void printAllBooks(String url, String user, String password) throws SQLException {
        String query = "SELECT * FROM Document";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String category = resultSet.getString("category");
                int availableCopies = resultSet.getInt("availableCopies");

                System.out.println("ID: " + id);
                System.out.println("Title: " + title);
                System.out.println("Author: " + author);
                System.out.println("Category: " + category);
                System.out.println("Available Copies: " + availableCopies);
                System.out.println("---------------------------");
            }
        }
    }

    // Static method to delete a book from the database based on its ID
    public static void deleteBookById(String url, String user, String password, int bookId) throws SQLException {
        String query = "DELETE FROM Document WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, bookId);
            preparedStatement.executeUpdate();
        }
    }
    // Method to search for books by title and return a list of IDs
    public static List<Integer> searchBooksByTitle(String url, String user, String password, String bookTitle) throws SQLException {
        String query = "SELECT id FROM Document WHERE title = ?";
        List<Integer> bookIds = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, bookTitle);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    bookIds.add(resultSet.getInt("id"));
                }
            }
        }
        return bookIds;
    }
    // Static method to print book information based on its ID
    public static void printBookById(String url, String user, String password, int bookId) throws SQLException {
        String query = "SELECT * FROM Document WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, bookId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String title = resultSet.getString("title");
                    String author = resultSet.getString("author");
                    String category = resultSet.getString("category");
                    int availableCopies = resultSet.getInt("availableCopies");

                    System.out.println("ID: " + bookId);
                    System.out.println("Title: " + title);
                    System.out.println("Author: " + author);
                    System.out.println("Category: " + category);
                    System.out.println("Available Copies: " + availableCopies);
                } else {
                    System.out.println("Book with ID " + bookId + " not found.");
                }
            }
        }
    }
    // Static method to print book information based on a list of IDs
    public static void printBooksByIds(String url, String user, String password, List<Integer> bookIds) throws SQLException {
        String query = "SELECT * FROM Document WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int bookId : bookIds) {
                preparedStatement.setInt(1, bookId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String title = resultSet.getString("title");
                        String author = resultSet.getString("author");
                        String category = resultSet.getString("category");
                        int availableCopies = resultSet.getInt("availableCopies");

                        System.out.println("ID: " + bookId);
                        System.out.println("Title: " + title);
                        System.out.println("Author: " + author);
                        System.out.println("Category: " + category);
                        System.out.println("Available Copies: " + availableCopies);
                        System.out.println("---------------------------");
                    } else {
                        System.out.println("Book with ID " + bookId + " not found.");
                    }
                }
            }
        }
    }
}
