package co.edu.uptc.repository;

import java.util.List;

public interface Repository<T> {
    void saveAll(List<T> data);
    void save(T entity);
    List<T> findAll();
}