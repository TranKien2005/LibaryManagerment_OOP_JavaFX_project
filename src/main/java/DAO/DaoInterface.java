package DAO;

import java.util.List;

public interface DaoInterface<T> {
    List<T> getAll();
    void insert(T t);
    void update(T t, int id);
    void delete(int id);
    T get(int id); // Sử dụng tham số id để lấy ra đối tượng
    int getID(T t);
    List<Integer> getAllID();
}