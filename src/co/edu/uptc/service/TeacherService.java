package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.util.List;

import co.edu.uptc.model.Teacher;
import co.edu.uptc.repository.JsonRepository;
import co.edu.uptc.repository.Repository;
import com.google.gson.reflect.TypeToken;

public class TeacherService {

    private final Repository<Teacher> repository;

    public TeacherService() {
        Type type = new TypeToken<List<Teacher>>() {}.getType();
        repository = new JsonRepository<>("teacher.json", type);
    }

    public List<Teacher> listar() {
        return repository.findAll();
    }

    public Teacher buscarPorId(String id) {
        for (Teacher teacher : repository.findAll()) {
            if (id != null && id.equals(teacher.getId())) {
                return teacher;
            }
        }
        return null;
    }
}
