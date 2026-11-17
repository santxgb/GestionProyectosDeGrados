package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import co.edu.uptc.exception.BusinessException;
import co.edu.uptc.model.Document;
import co.edu.uptc.model.Teacher;
import co.edu.uptc.repository.DataFiles;
import co.edu.uptc.repository.JsonRepository;
import co.edu.uptc.repository.Repository;
import com.google.gson.reflect.TypeToken;

public class TeacherService {

    private final Repository<Teacher> repository;
    private final Repository<Document> documentRepository;

    public TeacherService() {
        Type type = new TypeToken<List<Teacher>>() {}.getType();
        Type documentType = new TypeToken<List<Document>>() {}.getType();
        repository = new JsonRepository<>(DataFiles.TEACHERS, type);
        documentRepository = new JsonRepository<>(DataFiles.DOCUMENTS, documentType);
    }

    public TeacherService(Repository<Teacher> repository, Repository<Document> documentRepository) {
        this.repository = repository;
        this.documentRepository = documentRepository;
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

    public void save(Teacher teacher) {
        validateId(teacher);
        if (buscarPorId(teacher.getId()) != null) {
            throw new BusinessException("Ya existe un docente con el id " + teacher.getId() + ".");
        }
        repository.save(teacher);
    }

    public void update(Teacher teacher) {
        validateId(teacher);
        List<Teacher> teachers = repository.findAll();
        for (int i = 0; i < teachers.size(); i++) {
            if (teacher.getId().equals(teachers.get(i).getId())) {
                teachers.set(i, teacher);
                repository.saveAll(teachers);
                return;
            }
        }
        throw new BusinessException("No existe un docente con el id " + teacher.getId() + ".");
    }

    public void delete(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new BusinessException("Debe indicar el id del docente.");
        }
        List<Teacher> teachers = repository.findAll();
        for (int i = 0; i < teachers.size(); i++) {
            if (id.equals(teachers.get(i).getId())) {
                List<String> usedIn = documentsOf(id);
                if (!usedIn.isEmpty()) {
                    throw new BusinessException("No se puede eliminar al docente " + id
                            + " porque es director o codirector en los documentos: "
                            + String.join(", ", usedIn) + ".");
                }
                teachers.remove(i);
                repository.saveAll(teachers);
                return;
            }
        }
        throw new BusinessException("No existe un docente con el id " + id + ".");
    }

    private void validateId(Teacher teacher) {
        if (teacher == null) {
            throw new BusinessException("El docente no puede ser nulo.");
        }
        if (teacher.getId() == null || teacher.getId().trim().isEmpty()) {
            throw new BusinessException("El id del docente es obligatorio.");
        }
    }

    private List<String> documentsOf(String id) {
        List<String> ids = new ArrayList<>();
        for (Document document : documentRepository.findAll()) {
            if (id.equals(document.getDirectorId()) || id.equals(document.getCodirectorId())) {
                ids.add(document.getId());
            }
        }
        return ids;
    }
}