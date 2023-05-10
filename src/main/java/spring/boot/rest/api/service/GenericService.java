package spring.boot.rest.api.service;

import java.util.List;

public interface GenericService<T, H, K, ID> {
    T save(H entity);
    T update(K entity);
    T findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    void deleteAll();
}
