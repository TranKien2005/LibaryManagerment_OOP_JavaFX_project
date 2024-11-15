package googleAPI;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.text.Normalizer;
import java.util.regex.Pattern;
import util.*;

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
            URL url = new URL(testQuery);
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

    
    public static BookInfo getBookInfoByNoTruly (String bookTitle) {
        
        if (!isValidInput(bookTitle)) {
            return new BookInfo("Invalid input: Book title is required.", null, null);
        }
        if (!checkConnection()) {
            return new BookInfo("Connection error: Unable to connect to the server.", null, null);
        }

        try {
            String normalizedTitle = removeVietnameseAccents(bookTitle);
            String query = API_URL + "intitle:" + normalizedTitle.replace(" ", "+") + "&key=" + API_KEY;
            URL url = new URL(query);
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
                
                String description = "";
                JsonObject volumeInfo = items.get(0).getAsJsonObject().getAsJsonObject("volumeInfo");
                description += volumeInfo.has("description") ? volumeInfo.get("description").getAsString() : "No description found.";
                Double averageRating = volumeInfo.has("averageRating") ? volumeInfo.get("averageRating").getAsDouble() : null;
                String rating = averageRating != null ? "Average Rating: " + averageRating : "No rating found.";
                String imageUrl = volumeInfo.has("imageLinks") ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString() : "No image found.";
                return new BookInfo(description, rating, imageUrl, false);
            } else {
                return new BookInfo("No description found.", null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new BookInfo("Error occurred while fetching the book information.", null, null);
        }
    }


    
    public static BookInfo getBookInfo(String bookTitle) {
        if (!isValidInput(bookTitle)) {
            return new BookInfo("Invalid input: Book title is required.", null, null);
        }
        if (!checkConnection()) {
            return new BookInfo("Connection error: Unable to connect to the server.", null, null);
        }
        try {
            String query = API_URL + "intitle:" + bookTitle.replace(" ", "+") + "&key=" + API_KEY;
            URL url = new URL(query);
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
                        return new BookInfo(description, rating, imageUrl);
                    }
                }
                return new BookInfo("No description found.", null, null);
            } else {
                return new BookInfo("No description found.", null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
                return new BookInfo("Error occurred while fetching the book information.", null, null);
            }
        }
        

        
        


}


