package DAO;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import model.Document;

public final class BookDao implements DaoInterface<Document> {
    private static final String PATH = "src/main/java/data/book.json";
    private static BookDao instance;
    private final Gson gson;

    private BookDao() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public static BookDao getInstance() {
        if (instance == null) {
            instance = new BookDao();
        }
        return instance;
    }

    @Override
    public void insert(Document t) {
        List<Document> documents = getAll();
        documents.add(t);
        saveDocumentsToJson(documents);
    }

    @Override
    public void update(Document t) {
        List<Document> documents = getAll();
        for (int i = 0; i < documents.size(); i++) {
            if (documents.get(i).getName().equals(t.getName())) {
                documents.set(i, t);

                saveDocumentsToJson(documents);

                break;
            }
        }
        saveDocumentsToJson(documents);
    }

    @Override
    public void delete(Document t) {
        List<Document> documents = getAll();
        documents.removeIf(d -> d.getName().equals(t.getName()));
        saveDocumentsToJson(documents);
    }

    @Override
    public Document get(Document t) {
       List<Document> documents = getAll();
       for (Document document : documents) {
        if (document.getName().equals(t.getName())) {
            return document;
        }
       }
       return null;
    }

    @Override
    public List<Document> getAll() {
        List<Document> documents = new ArrayList<>();
        try (JsonReader reader = new JsonReader(new FileReader(PATH))) {
            reader.beginArray();
            while (reader.hasNext()) {
                Document document = gson.fromJson(reader, Document.class);
                documents.add(document);
            }
            reader.endArray();
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
        return documents;
    }

    private void saveDocumentsToJson(List<Document> documents) {
        try( FileWriter writer = new FileWriter(PATH)) {
          gson.toJson(documents,writer);
        } catch (IOException e) {
            System.err.println("Error writing to JSON file: " + e.getMessage());
        }
    }


    public boolean add(Document document) {
        try {
            insert(document);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Document getByName(String name) {
        List<Document> documents = getAll();
        for (Document document : documents) {
            if (document.getName().equals(name)) {
                return document;
            }
        }
        return null;

    }
}
