package googleAPI;

public class BookInfo {
    private String description;
    private String rating;
    private String imageUrl;
    public boolean truly = true;

    public BookInfo(String description, String rating, String imageUrl) {
        this.description = description;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    public BookInfo(String description, String rating, String imageUrl, boolean truly) {
        this.description = description;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.truly = truly;
    }

    public String getDescription() {
        return description;
    }

    public String getRating() {
        return rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}