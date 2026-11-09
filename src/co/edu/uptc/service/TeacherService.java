package co.edu.uptc.service;


import java.lang.reflect.Type;
import java.util.List;

import co.edu.uptc.model.Teacher;
import co.edu.uptc.repository.JsonRepository;
import com.google.gson.reflect.TypeToken;

public class TeacherService {

     public TeacherService() {
        Type type = new TypeToken<List<Teacher>>() {}.getType();
        repository = new JsonRepository<>("teacher.json", type);
    }

    public List<Teacher> listar() {
        return repository.findAll();
    }

    public Teacher buscarPorId(String id) {

        return repository.findAll()
                .stream()
                .filter(m -> m.getId()==id)
                .findFirst()
                .orElse(null);
    }
}
