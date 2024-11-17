package googleAPI;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import model.Document;

import java.text.Normalizer;
import java.util.regex.Pattern;
import util.*;
import javafx.util.Pair;

public class GoogleApiBookController {

    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String API_KEY = "AIzaSyA06bI-XxA2j5ucnBbbN5fdrGux_flXDjc";
    // Hàm kiểm tra đầu vào
    private static boolean isValidInput(String bookTitle) {
        if (bookTitle == null || bookTitle.trim().isEmpty()) {
            System.out.println("Invalid input: Book title is required.");
            return false;
            }
        
       
        return true;
       
    }


    private static boolean checkConnection() {
        try {
            String testQuery = API_URL + "test" + "&key=" + API_KEY;
            URI uri = new URI(testQuery);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode); // Thêm thông báo gỡ lỗi
    
            if (responseCode != 200) {
                ErrorDialog.showError("Connection Error", "Cannot connect to the server. Response code: " + responseCode, null);
                return false;
            }
        } catch (java.net.MalformedURLException e) {
            ErrorDialog.showError("URL Error", "The URL is malformed: " + e.getMessage(), null);
            return false;
        } catch (java.io.IOException e) {
            ErrorDialog.showError("IO Error", "An I/O error occurred: " + e.getMessage(), null);
            return false;
        } catch (Exception e) {
            ErrorDialog.showError("Unknown Error", "An unknown error occurred: " + e.getMessage(), null);
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
        if (!checkConnection()) {
            return new BookInfo("Connection error: Unable to connect to the server.", null, null, null);
        }
        try {
            String query = API_URL + "intitle:" + bookTitle.replace(" ", "+") + "&key=" + API_KEY;
            URI uri = new URI(query);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
    
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
    
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
    
            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray items = jsonResponse.getAsJsonArray("items");
    
            if (items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    JsonObject volumeInfo = items.get(i).getAsJsonObject().getAsJsonObject("volumeInfo");
                    String title = volumeInfo.get("title").getAsString();
                    if (removeVietnameseAccents(title.toLowerCase()).equals(removeVietnameseAccents(bookTitle.toLowerCase()))) {
                        String description = volumeInfo.has("description") ? volumeInfo.get("description").getAsString() : "No description found.";
                        if (description.equals("No description found.")) {
                            continue;
                        }
                        Double averageRating = volumeInfo.has("averageRating") ? volumeInfo.get("averageRating").getAsDouble() : null;
                        String rating = averageRating != null ? "Average Rating: " + averageRating : "No rating found.";
                        String imageUrl = volumeInfo.has("imageLinks") ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString() : "No image found.";
                        String reviewCount = volumeInfo.has("ratingsCount") ? volumeInfo.get("ratingsCount").getAsString() : "No review count found.";
                        return new BookInfo(description, rating, imageUrl, reviewCount);
                    }
                }
                return new BookInfo("No description found.", null, null, null);
            } else {
                return new BookInfo("No description found.", null, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("No Results", "Can't find book with that IBSN: "+ e.getMessage(), null);
            return new BookInfo("Error occurred while fetching the book information.", null, null, null);
        }
    }
        

        
        public static Document getBookInfoByISBN(String isbn) {
            if (isbn == null || isbn.trim().isEmpty()) {
                ErrorDialog.showError("Invalid input", "ISBN is required.", null);
                return null;
            }
            if (!checkConnection()) {
                ErrorDialog.showError("Connection error", "Unable to connect to the server.", null);
                return null;
            }
            try {
                String query = API_URL + "isbn:" + isbn + "&key=" + API_KEY;
                URI uri = new URI(query);
                URL url = uri.toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
    
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
    
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
    
                JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
                JsonArray items = jsonResponse.getAsJsonArray("items");
    
                if (items.size() > 0) {
                    JsonObject volumeInfo = items.get(0).getAsJsonObject().getAsJsonObject("volumeInfo");
                    String title = volumeInfo.get("title").getAsString();
                    String author = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").get(0).getAsString() : "Unknown author";
                    String category = volumeInfo.has("categories") ? volumeInfo.getAsJsonArray("categories").get(0).getAsString() : "Unknown category";
                    String publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").getAsString() : "Unknown publisher";
                    int yearPublished = volumeInfo.has("publishedDate") ? Integer.parseInt(volumeInfo.get("publishedDate").getAsString().substring(0, 4)) : 0;
                    int availableCopies = 100; // Assuming 1 copy is available by default
                   
                    String description = volumeInfo.has("description") ? volumeInfo.get("description").getAsString() : "No description available";
                    double averageRating = volumeInfo.has("averageRating") ? volumeInfo.get("averageRating").getAsDouble() : 0.0;
                    int ratingsCount = volumeInfo.has("ratingsCount") ? volumeInfo.get("ratingsCount").getAsInt() : 0;
                    String imageUrl = volumeInfo.has("imageLinks") ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString() : "No image available";
                    
                Document document = new Document(title, author, category, publisher, yearPublished, availableCopies);
                document.setDescription(description);
                document.setRating(averageRating);
                document.setReviewCount(ratingsCount);
                document.setCoverImageByUrl(imageUrl);
                return document;
                } 
                else {
                    ErrorDialog.showError("No Results", "No book information found for the provided ISBN.", null);
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                ErrorDialog.showError("No Results", "Can't find book with that IBSN: "+ e.getMessage(), null);
                return null;
            }
        }
    }




