package model;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;

public final class Document {
    private String title;
    private String author;
    private String category;
    private String publisher;
    private int yearPublished;
    private int availableCopies;
    private int bookID;
    private String description;
    private double rating;
    private int reviewCount;
    private InputStream coverImage;

    public Document(String title, String author, String category, String publisher, int yearPublished,
            int availableCopies) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.publisher = publisher;
        this.yearPublished = yearPublished;
        this.availableCopies = availableCopies;
    }

    public Document(int bookID, String title, String author, String category, String publisher, int yearPublished,
            int availableCopies) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.publisher = publisher;
        this.yearPublished = yearPublished;
        this.availableCopies = availableCopies;
        this.bookID = bookID;
    }

    public int getBookID() {
        return bookID;
    }

    // Getters and setters
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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(int yearPublished) {
        this.yearPublished = yearPublished;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public InputStream getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(InputStream coverImage) {
        this.coverImage = coverImage;
    }

    public void setCoverImageByUrl(String urlString) throws IOException {
        @SuppressWarnings("deprecation")
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            setCoverImage(inputStream);
        } else {
            throw new IOException("Failed to fetch image from URL: " + connection.getResponseMessage());
        }
    }
}