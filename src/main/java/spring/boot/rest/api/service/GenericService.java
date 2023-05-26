package spring.boot.rest.api.service;

import java.util.List;

public interface GenericService<T, ID> {
    T save(T entity);
    T update(T entity);
    T findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    void deleteAll();
}
