package test;

public class Document {
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
}
