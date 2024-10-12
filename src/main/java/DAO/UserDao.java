package DAO;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import model.User;

public class UserDao implements DaoInterface<User>{
    private static final String filePath = "src/main/resources/user.json";
    private static UserDao instance;

    private UserDao() {}

    public static UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao();
        }
        return instance;
    }


    @Override
    public void insert(User t) {
        List<User> users = getAll();
        users.add(t);
        saveUsersToJson(users);
    }

    @Override
    public void update(User t) {
        List<User> users = getAll();
        for (User user : users) {
            if (user.getUsername().equals(t.getUsername())) {
                user = t;
            }
        }
        saveUsersToJson(users);
    }

    @Override
    public void delete(User t) {
        List<User> users = getAll();
        users.removeIf(user -> user.getUsername().equals(t.getUsername()));
        saveUsersToJson(users);
    }

    @Override
    public User get(User t) {
        List<User> users = getAll();
        for (User user : users) {
            if (user.getUsername().equals(t.getUsername())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> getAll() {
        Gson gson = new Gson();
        Type userListType = new TypeToken<List<User>>(){}.getType();
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, userListType);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    private void saveUsersToJson(List<User> users) {
        Gson gson = new Gson();
        String json = gson.toJson(users);
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
