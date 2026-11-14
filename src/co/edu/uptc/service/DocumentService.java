package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

import co.edu.uptc.model.Document;
import co.edu.uptc.model.enums.Modality;
import co.edu.uptc.model.enums.Stage;
import co.edu.uptc.model.enums.Status;
import co.edu.uptc.repository.JsonRepository;
import co.edu.uptc.repository.Repository;
import com.google.gson.reflect.TypeToken;

public class DocumentService {

    private final Repository<Document> repository;
    private final StudentService studentService;
    private final TeacherService teacherService;

    public DocumentService() {
        Type type = new TypeToken<List<Document>>() {}.getType();
        repository = new JsonRepository<>("document.json", type);
        studentService = new StudentService();
        teacherService = new TeacherService();
    }

    public DocumentService(Repository<Document> repository,
                           StudentService studentService, TeacherService teacherService) {
        this.repository = repository;
        this.studentService = studentService;
        this.teacherService = teacherService;
    }

    public List<Document> listar() {
        return repository.findAll();
    }

    public Document buscarPorId(String id) {
        for (Document document : repository.findAll()) {
            if (id != null && id.equals(document.getId())) {
                return document;
            }
        }
        return null;
    }

    public boolean save(Document document) {
        if (!isValidDocument(document) || !isDraft(document)
                || buscarPorId(document.getId()) != null) {
            return false;
        }
        repository.save(document);
        return true;
    }

    public boolean update(Document document) {
        if (!isValidDocument(document) || !isDraft(document)) {
            return false;
        }
        List<Document> documents = repository.findAll();
        for (int i = 0; i < documents.size(); i++) {
            Document previous = documents.get(i);
            if (document.getId().equals(previous.getId())) {
                if (!isDraft(previous)) {
                    return false;
                }
                documents.set(i, document);
                repository.saveAll(documents);
                return true;
            }
        }
        return false;
    }

    public boolean delete(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        List<Document> documents = repository.findAll();
        for (int i = 0; i < documents.size(); i++) {
            Document document = documents.get(i);
            if (id.equals(document.getId())) {
                if (!isDraft(document)) {
                    return false;
                }
                documents.remove(i);
                repository.saveAll(documents);
                return true;
            }
        }
        return false;
    }

    public boolean registerDecision(String id, Status status, LocalDate date, String act) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        List<Document> documents = repository.findAll();
        for (Document document : documents) {
            if (id.equals(document.getId())) {
                if (!isValidDocument(document) || !document.registerDecision(status, date, act)) {
                    return false;
                }
                repository.saveAll(documents);
                return true;
            }
        }
        return false;
    }

    private boolean isDraft(Document document) {
        return document != null && document.getStage() == Stage.PROPUESTA
                && document.getStatus() == Status.EN_REVISION
                && document.getDecisionDate() == null
                && document.getDegreeWork() == null
                && document.getStatusAct() == null;
    }

    private boolean isValidDocument(Document document) {
        if (document == null || document.getId() == null || document.getId().trim().isEmpty()
                || document.getTitle() == null || document.getTitle().trim().isEmpty()
                || document.getModality() == null || document.getFilingDate() == null
                || document.getStudentIds() == null
                || document.getStudentIds().isEmpty() || document.getStudentIds().size() > 2
                || document.getDirectorId() == null || document.getDirectorId().trim().isEmpty()
                || teacherService.buscarPorId(document.getDirectorId()) == null) {
            return false;
        }

        List<String> studentIds = document.getStudentIds();
        for (int i = 0; i < studentIds.size(); i++) {
            String code = studentIds.get(i);
            if (code == null || code.trim().isEmpty() || studentService.buscarPorId(code) == null) {
                return false;
            }
            for (int j = i + 1; j < studentIds.size(); j++) {
                if (code.equals(studentIds.get(j))) {
                    return false;
                }
            }
        }

        String codirectorId = document.getCodirectorId();
        if (codirectorId != null && !codirectorId.trim().isEmpty()) {
            if (codirectorId.equals(document.getDirectorId())
                    || teacherService.buscarPorId(codirectorId) == null) {
                return false;
            }
        }

        if (document.getCompany() != null
                && document.getModality() != Modality.PRACTICA_EMPRESARIAL_O_SOCIAL) {
            return false;
        }
        return true;
    }
}
