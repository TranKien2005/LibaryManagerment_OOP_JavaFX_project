package googleAPI;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import model.Document;

import java.text.Normalizer;
import java.util.regex.Pattern;
import util.*;

public class GoogleApiBookController {

    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String API_KEY = "AIzaSyA06bI-XxA2j5ucnBbbN5fdrGux_flXDjc";
    private static final String API_KEY1 = "AIzaSyDZzdNOpuwPTo5Cf168Q5cWrSfrEyzihG4";
    private static final String API_KEY2 = "AIzaSyCTE6oj8jckIAisFXwGj-5FoviTYmU5zMA";
    private static final String API_KEY3 = "AIzaSyBnmowHa3OTFVUeoq-LShwpXJ9GPtZ4XpU";

    private static final String[] API_KEYS = { API_KEY, API_KEY1, API_KEY2, API_KEY3 };
    private static int currentKeyIndex = 0;

    private static String getNextApiKey() {
        currentKeyIndex = (currentKeyIndex + 1) % API_KEYS.length;
        return API_KEYS[currentKeyIndex];
    }

    // Hàm kiểm tra đầu vào
    private static boolean isValidInput(String bookTitle) {
        if (bookTitle == null || bookTitle.trim().isEmpty()) {
            System.out.println("Invalid input: Book title is required.");
            return false;
        }

        return true;

    }

    // Hàm loại bỏ dấu tiếng Việt
    private static String removeVietnameseAccents(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }

    public static BookInfo getBookInfo(String bookTitle) {
        if (!isValidInput(bookTitle)) {
            return new BookInfo("Invalid input: Book title is required.", null, null, null);
        }

        try {
            String query = API_URL + "intitle:" + bookTitle.replace(" ", "+") + "&key=" + API_KEY;
            URI uri = new URI(query);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            System.out.println("Response code: " + responseCode);
            if (responseCode != 200) {
                throw new RuntimeException("Connection error: " + responseCode);
            }

            StringBuilder response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray items = jsonResponse.getAsJsonArray("items");

            if (items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    JsonObject volumeInfo = items.get(i).getAsJsonObject().getAsJsonObject("volumeInfo");
                    String title = volumeInfo.get("title").getAsString();
                    if (removeVietnameseAccents(title.toLowerCase())
                            .equals(removeVietnameseAccents(bookTitle.toLowerCase()))) {
                        String description = volumeInfo.has("description") ? volumeInfo.get("description").getAsString()
                                : "No description found.";

                        Double averageRating = volumeInfo.has("averageRating")
                                ? volumeInfo.get("averageRating").getAsDouble()
                                : null;
                        String rating = averageRating != null ? "Average Rating: " + averageRating : "No rating found.";
                        String imageUrl = volumeInfo.has("imageLinks")
                                ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString()
                                : "No image found.";
                        String reviewCount = volumeInfo.has("ratingsCount")
                                ? volumeInfo.get("ratingsCount").getAsString()
                                : "No review count found.";
                        return new BookInfo(description, rating, imageUrl, reviewCount);
                    }
                }
                throw new Exception("No book found with the title: " + bookTitle);
            } else {
                throw new Exception("No book found with the title: " + bookTitle);
            }
        } catch (Exception e) {

            throw new RuntimeException("Unknown bug:" + e.getMessage(), e);
        }
    }

    public static Document getBookInfoByISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            ErrorDialog.showError("Invalid input", "ISBN is required.", null);
            return null;
        }

        try {
            String query = API_URL + "isbn:" + isbn + "&key=" + getNextApiKey();
            URI uri = new URI(query);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("Response code: " + responseCode);
            if (responseCode != 200) {
                throw new RuntimeException("Connection error: " + responseCode);
            }

            StringBuilder response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray items = jsonResponse.getAsJsonArray("items");

            if (items != null && items.size() > 0) {
                JsonObject volumeInfo = items.get(0).getAsJsonObject().getAsJsonObject("volumeInfo");
                String title = volumeInfo.get("title").getAsString();
                String author = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").get(0).getAsString()
                        : "Unknown author";
                String category = volumeInfo.has("categories")
                        ? volumeInfo.getAsJsonArray("categories").get(0).getAsString()
                        : "Unknown category";
                String publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").getAsString()
                        : "Unknown publisher";
                int yearPublished = volumeInfo.has("publishedDate")
                        ? Integer.parseInt(volumeInfo.get("publishedDate").getAsString().substring(0, 4))
                        : 0;
                int availableCopies = 100; // Assuming 1 copy is available by default

                String description = volumeInfo.has("description") ? volumeInfo.get("description").getAsString()
                        : "No description available";
                double averageRating = volumeInfo.has("averageRating") ? volumeInfo.get("averageRating").getAsDouble()
                        : 0.0;
                int ratingsCount = volumeInfo.has("ratingsCount") ? volumeInfo.get("ratingsCount").getAsInt() : 0;
                String imageUrl = volumeInfo.has("imageLinks")
                        ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString()
                        : "No image available";

                Document document = new Document(title, author, category, publisher, yearPublished, availableCopies);
                document.setDescription(description);
                document.setRating(averageRating);
                document.setReviewCount(ratingsCount);
                try {
                    document.setCoverImageByUrl(imageUrl);
                } catch (IOException e) {
                    document.setCoverImageByUrl(null);
                }
                return document;
            } else {
                throw new Exception("No book information found for the provided ISBN.");
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI Syntax Error: " + e.getMessage(), e);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL Error: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("IO Error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unknown Error: " + e.getMessage(), e);
        }
    }
}
