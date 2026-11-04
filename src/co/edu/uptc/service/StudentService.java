package co.edu.uptc.service;

import java.util.List;

import co.edu.uptc.model.Student;
import co.edu.uptc.repository.JsonRepository;

public class StudentService {
    public StudentService() {
        Type type = new TypeToken<List<Student>>() {
        }.getType();
        repository = new JsonRepository<>("student.json", type);
    }
    public List<Student> listar() {
        return repository.findAll();
    }

    public Student buscarPorId(String id) {

        return repository.findAll()
                .stream()
                .filter(m -> m.getId()==id)
                .findFirst()
                .orElse(null);
    }
}
