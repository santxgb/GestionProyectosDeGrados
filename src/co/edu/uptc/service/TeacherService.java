package co.edu.uptc.service;

import java.util.List;

import co.edu.uptc.model.Teacher;
import co.edu.uptc.repository.Repository;

public class TeacherService {

    private final Repository<Teacher> repository;

    public TeacherService(Repository<Teacher> repository) {
        this.repository = repository;
    }

    public List<Teacher> findAll() {
        return repository.findAll();
    }

    public Teacher findById(String id) {
        return repository.findAll()
                .stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean create(Teacher teacher) {
        if (findById(teacher.getId()) != null) {
            return false;
        }
        repository.save(teacher);
        return true;
    }

    public boolean update(Teacher teacher) {
        List<Teacher> teachers = repository.findAll();
        for (int i = 0; i < teachers.size(); i++) {
            if (teachers.get(i).getId().equals(teacher.getId())) {
                teachers.set(i, teacher);
                repository.saveAll(teachers);
                return true;
            }
        }
        return false;
    }

    public boolean delete(String id) {
        List<Teacher> teachers = repository.findAll();
        boolean removed = teachers.removeIf(t -> t.getId().equals(id));
        if (removed) {
            repository.saveAll(teachers);
        }
        return removed;
    }
}