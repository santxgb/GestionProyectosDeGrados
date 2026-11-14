package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.util.List;

import co.edu.uptc.model.Student;
import co.edu.uptc.repository.JsonRepository;
import co.edu.uptc.repository.Repository;
import com.google.gson.reflect.TypeToken;

public class StudentService {

    private final Repository<Student> repository;

    public StudentService() {
        Type type = new TypeToken<List<Student>>() {}.getType();
        repository = new JsonRepository<>("student.json", type);
    }

    public List<Student> listar() {
        return repository.findAll();
    }

    public Student buscarPorId(String id) {
        for (Student student : repository.findAll()) {
            if (id != null && id.equals(student.getCode())) {
                return student;
            }
        }
        return null;
    }

    public boolean save(Student student) {
        if (!hasValidCode(student)) {
            return false;
        }
        if (buscarPorId(student.getCode()) != null) {
            return false;
        }
        repository.save(student);
        return true;
    }

    public boolean update(Student student) {
        if (!hasValidCode(student)) {
            return false;
        }
        List<Student> students = repository.findAll();
        for (int i = 0; i < students.size(); i++) {
            if (student.getCode().equals(students.get(i).getCode())) {
                students.set(i, student);
                repository.saveAll(students);
                return true;
            }
        }
        return false;
    }

    public boolean delete(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        List<Student> students = repository.findAll();
        for (int i = 0; i < students.size(); i++) {
            if (code.equals(students.get(i).getCode())) {
                students.remove(i);
                repository.saveAll(students);
                return true;
            }
        }
        return false;
    }

    private boolean hasValidCode(Student student) {
        return student != null && student.getCode() != null
                && !student.getCode().trim().isEmpty();
    }
}
