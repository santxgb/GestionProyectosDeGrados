package co.edu.uptc.model.repository;

import java.util.List;

public interface Repository<T> {
    void saveAll(List<T> data);
    void save(T entity);
    List<T> findAll();
}