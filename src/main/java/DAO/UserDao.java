package DAO;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import model.User;

public final class UserDao implements DaoInterface<User>{
    private static final String filePath = "src/main/java/data/user.json";
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
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId() == t.getUserId()) {
                users.set(i, t);
                break;
            }
        }
        saveUsersToJson(users);
    }

    @Override
    public void delete(User t) {
        List<User> users = getAll();
        users.removeIf(user -> user.getUserId() == t.getUserId());
        saveUsersToJson(users);
    }

    @Override
    public User get(User t) {
        List<User> users = getAll();
        for (User user : users) {
            if (user.getUserId() == t.getUserId()) {
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
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
