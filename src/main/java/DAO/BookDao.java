package DAO;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import model.Document;

public final class BookDao implements DaoInterface<Document> {
    private static final String PATH = "src/main/java/data/book.json";
    private static BookDao instance;

    private BookDao() {}

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
        for (Document document : documents) {
            if (document.getName().equals(t.getName())) {
                document = t;
            }
        }
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
        try (FileReader reader = new FileReader(PATH)) {
            documents = new Gson().fromJson(reader, new TypeToken<List<Document>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return documents;
    }

    private void saveDocumentsToJson(List<Document> documents) {
        try (FileWriter writer = new FileWriter(PATH)) {
            new Gson().toJson(documents, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
