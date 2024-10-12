package model;
import java.util.List;

import DAO.UserDao;
import thread.ThreadManager;
public final class User {
    private static int nextId = 6;
    private int userId; 
    private String username;
    private String password;
    private String address;
    private String phone;
    private String email;
    private String sex;
    private int age;

    static {
        ThreadManager.execute(() -> {
            List<User>user = UserDao.getInstance().getAll();
            nextId = user.getLast().getUserId() + 1;
        });
        ThreadManager.shutdown();  
    }
    
    public User(String username, String password, String address, String phone, String email, String sex, int age) {
        this.userId = nextId++;
        this.username = username;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.sex = sex;
        this.age = age;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
