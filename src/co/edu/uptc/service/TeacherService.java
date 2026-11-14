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

    public boolean save(Teacher teacher) {
        if (!hasValidId(teacher)) {
            return false;
        }
        if (buscarPorId(teacher.getId()) != null) {
            return false;
        }
        repository.save(teacher);
        return true;
    }

    public boolean update(Teacher teacher) {
        if (!hasValidId(teacher)) {
            return false;
        }
        List<Teacher> teachers = repository.findAll();
        for (int i = 0; i < teachers.size(); i++) {
            if (teacher.getId().equals(teachers.get(i).getId())) {
                teachers.set(i, teacher);
                repository.saveAll(teachers);
                return true;
            }
        }
        return false;
    }

    public boolean delete(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        List<Teacher> teachers = repository.findAll();
        for (int i = 0; i < teachers.size(); i++) {
            if (id.equals(teachers.get(i).getId())) {
                teachers.remove(i);
                repository.saveAll(teachers);
                return true;
            }
        }
        return false;
    }

    private boolean hasValidId(Teacher teacher) {
        return teacher != null && teacher.getId() != null
                && !teacher.getId().trim().isEmpty();
    }
}
