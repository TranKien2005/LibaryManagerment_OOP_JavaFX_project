package DAO;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import model.Borrow;

public class BorrowDao implements DaoInterface<Borrow> {
    private static final String FILE_PATH = "src/main/java/data/borrow.json";
    private static BorrowDao instance;
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .create();

    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.toString());
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.getAsString());
        }
    }
    private BorrowDao() {}

    public static BorrowDao getInstance() {
        if (instance == null) {
            instance = new BorrowDao();
        }
        return instance;
    }

    @Override
    public void insert(Borrow borrow) {
        List<Borrow> borrows = getAll();
        borrows.add(borrow);
        saveBorrowsToJson(borrows);
    }

    @Override
    public void update(Borrow borrow) {
        List<Borrow> borrows = getAll();
        for (int i = 0; i < borrows.size(); i++) {
            if (borrows.get(i).getUser_id() == borrow.getUser_id() && 
                borrows.get(i).getBookname().equals(borrow.getBookname())) {
                borrows.set(i, borrow);
                break;
            }
        }
        saveBorrowsToJson(borrows);
    }

    @Override
    public void delete(Borrow borrow) {
        List<Borrow> borrows = getAll();
        borrows.removeIf(b -> b.equals(borrow));
        saveBorrowsToJson(borrows);
    }

    @Override
    public Borrow get(Borrow borrow) {
        List<Borrow> borrows = getAll();
        for (Borrow b : borrows) {
            if (b.getUser_id() == borrow.getUser_id() && 
                b.getBookname().equals(borrow.getBookname())) {
                return b;
            }
        }
        return null;
    }

    @Override
    public List<Borrow> getAll() {
        List<Borrow> borrows = new ArrayList<>();
        try (Reader reader = new FileReader(FILE_PATH)) {
            if (reader.ready()) {
                String jsonContent = readFileContent(reader);
                if (!jsonContent.trim().isEmpty()) {
                    Type listType = new TypeToken<ArrayList<Borrow>>(){}.getType();
                    borrows = gson.fromJson(jsonContent, listType);
                } else {
                    System.out.println("The JSON file is empty.");
                }
            }
        } catch (JsonSyntaxException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
        return borrows;
    }

    private void saveBorrowsToJson(List<Borrow> borrows) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            Gson prettyGson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
            prettyGson.toJson(borrows, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFileContent(Reader reader) throws IOException {
        StringBuilder content = new StringBuilder();
        char[] buffer = new char[1024];
        int charsRead;
        while ((charsRead = reader.read(buffer)) != -1) {
            content.append(buffer, 0, charsRead);
        }
        return content.toString();
    }
}


