package googleAPI;

public class BookInfo {
    private String description;
    private String rating;
    private String imageUrl;
    private String reviewCount;

    public BookInfo(String description, String rating, String imageUrl, String reviewCount) {
        this.description = description;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.reviewCount = reviewCount;
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

    public String getReviewCount() {
        return reviewCount;
    }
}