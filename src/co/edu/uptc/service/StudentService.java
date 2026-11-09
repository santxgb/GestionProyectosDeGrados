package co.edu.uptc.service;

import java.util.List;

import co.edu.uptc.model.Student;
import co.edu.uptc.repository.Repository;

public class StudentService {

    private final Repository<Student> repository;

    public StudentService(Repository<Student> repository) {
        this.repository = repository;
    }

    public List<Student> findAll() {
        return repository.findAll();
    }

    public Student findById(String id) {
        return repository.findAll()
                .stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean create(Student student) {
        if (findById(student.getId()) != null) {
            return false;
        }
        repository.save(student);
        return true;
    }

    public boolean update(Student student) {
        List<Student> students = repository.findAll();
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId().equals(student.getId())) {
                students.set(i, student);
                repository.saveAll(students);
                return true;
            }
        }
        return false;
    }

    public boolean delete(String id) {
        List<Student> students = repository.findAll();
        boolean removed = students.removeIf(s -> s.getId().equals(id));
        if (removed) {
            repository.saveAll(students);
        }
        return removed;
    }
}