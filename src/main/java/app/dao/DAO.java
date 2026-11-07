package app.dao;

import java.util.List;

public interface DAO<T> {
    boolean create(T entity);

    boolean update(T entity);

    boolean delete(long id);

    List<T> selectAll();

    default List<T> selectAll(long userId) {
        throw new UnsupportedOperationException("User-scoped selection is not supported.");
    }

    T selectOne(long id);
}
