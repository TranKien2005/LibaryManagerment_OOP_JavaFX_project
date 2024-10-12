package DAO;
import java.util.List;
public interface DaoInterface<T> {
    public void insert(T t);
    public void update(T t);
    public void delete(T t);
    public T get(T t);
    public List<T> getAll();
}
