package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import co.edu.uptc.exception.BusinessException;
import co.edu.uptc.model.Document;
import co.edu.uptc.model.Student;
import co.edu.uptc.repository.DataFiles;
import co.edu.uptc.repository.JsonRepository;
import co.edu.uptc.repository.Repository;
import com.google.gson.reflect.TypeToken;

public class StudentService {

    private final Repository<Student> repository;
    private final Repository<Document> documentRepository;

    public StudentService() {
        Type type = new TypeToken<List<Student>>() {}.getType();
        Type documentType = new TypeToken<List<Document>>() {}.getType();
        repository = new JsonRepository<>(DataFiles.STUDENTS, type);
        documentRepository = new JsonRepository<>(DataFiles.DOCUMENTS, documentType);
    }

    public StudentService(Repository<Student> repository, Repository<Document> documentRepository) {
        this.repository = repository;
        this.documentRepository = documentRepository;
    }

    public List<Student> listar() {
        return repository.findAll();
    }

    public Student buscarPorCodigo(String code) {
        for (Student student : repository.findAll()) {
            if (code != null && code.equals(student.getCode())) {
                return student;
            }
        }
        return null;
    }

    public void save(Student student) {
        validateCode(student);
        if (buscarPorCodigo(student.getCode()) != null) {
            throw new BusinessException("Ya existe un estudiante con el código " + student.getCode() + ".");
        }
        repository.save(student);
    }

    public void update(Student student) {
        validateCode(student);
        List<Student> students = repository.findAll();
        for (int i = 0; i < students.size(); i++) {
            if (student.getCode().equals(students.get(i).getCode())) {
                students.set(i, student);
                repository.saveAll(students);
                return;
            }
        }
        throw new BusinessException("No existe un estudiante con el código " + student.getCode() + ".");
    }

    public void delete(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new BusinessException("Debe indicar el código del estudiante.");
        }
        List<Student> students = repository.findAll();
        for (int i = 0; i < students.size(); i++) {
            if (code.equals(students.get(i).getCode())) {
                List<String> usedIn = documentsOf(code);
                if (!usedIn.isEmpty()) {
                    throw new BusinessException("No se puede eliminar al estudiante " + code
                            + " porque aparece en los documentos: " + String.join(", ", usedIn) + ".");
                }
                students.remove(i);
                repository.saveAll(students);
                return;
            }
        }
        throw new BusinessException("No existe un estudiante con el código " + code + ".");
    }

    private void validateCode(Student student) {
        if (student == null) {
            throw new BusinessException("El estudiante no puede ser nulo.");
        }
        if (student.getCode() == null || student.getCode().trim().isEmpty()) {
            throw new BusinessException("El código del estudiante es obligatorio.");
        }
    }

    private List<String> documentsOf(String code) {
        List<String> ids = new ArrayList<>();
        for (Document document : documentRepository.findAll()) {
            if (document.getStudentIds() != null && document.getStudentIds().contains(code)) {
                ids.add(document.getId());
            }
        }
        return ids;
    }
}
