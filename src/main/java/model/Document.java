package model;

public final class Document {
    private String name;
    private String author;
    private String category;
    private String publisher;
    private int year;
    private int quantity;
    public Document(String name, String author, String category, String publisher, int year, int quantity) {
        this.name = name;
        this.author = author;
        this.category = category;
        this.publisher = publisher;
        this.year = year;
        this.quantity = quantity;
    }
    public String getName() {
        return name;
    }
    public String getAuthor() {
        return author;
    }
    public String getCategory() {
        return category;
    }
    public String getPublisher() {
        return publisher;
    }
    public int getYear() {
        return year;
    }

    public int getQuantity() {
        return quantity;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
