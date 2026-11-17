package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import co.edu.uptc.exception.BusinessException;
import co.edu.uptc.model.Alert;
import co.edu.uptc.model.Attachment;
import co.edu.uptc.model.DegreeWork;
import co.edu.uptc.model.Document;
import co.edu.uptc.model.Jury;
import co.edu.uptc.model.enums.AlertType;
import co.edu.uptc.model.enums.DeadlineStatus;
import co.edu.uptc.model.enums.Modality;
import co.edu.uptc.model.enums.Stage;
import co.edu.uptc.model.enums.Status;
import co.edu.uptc.repository.DataFiles;
import co.edu.uptc.repository.JsonRepository;
import co.edu.uptc.repository.Repository;
import com.google.gson.reflect.TypeToken;

public class DocumentService {

    private static final int PROPOSAL_WARNING_DAYS = 3;

    private final Repository<Document> repository;
    private final StudentService studentService;
    private final TeacherService teacherService;

    public DocumentService() {
        Type type = new TypeToken<List<Document>>() {}.getType();
        repository = new JsonRepository<>(DataFiles.DOCUMENTS, type);
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
        validateDocumentId(id);
        for (Document document : repository.findAll()) {
            if (id != null && id.equals(document.getId())) {
                return document;
            }
        }
        return null;
    }

    // ---------- Propuestas (borradores) ----------

    public void save(Document document) {
        validateDocument(document);
        if (!isDraft(document)) {
            throw new BusinessException("Solo se puede registrar un documento nuevo como propuesta en revisión.");
        }
        if (buscarPorId(document.getId()) != null) {
            throw new BusinessException("Ya existe un documento con el id " + document.getId() + ".");
        }
        repository.save(document);
    }

    public void update(Document document) {
        validateDocument(document);
        if (!isDraft(document)) {
            throw new BusinessException("Solo se puede editar como propuesta un documento en revisión.");
        }
        List<Document> documents = repository.findAll();
        for (int i = 0; i < documents.size(); i++) {
            Document previous = documents.get(i);
            if (document.getId().equals(previous.getId())) {
                if (!isDraft(previous)) {
                    throw new BusinessException("El documento ya tiene una decisión registrada; "
                            + "use las operaciones de seguimiento del trabajo de grado.");
                }
                documents.set(i, document);
                repository.saveAll(documents);
                return;
            }
        }
        throw new BusinessException("No existe un documento con el id " + document.getId() + ".");
    }

    public void delete(String id) {
        validateDocumentId(id);
        List<Document> documents = repository.findAll();
        for (int i = 0; i < documents.size(); i++) {
            Document document = documents.get(i);
            if (id.equals(document.getId())) {
                if (!isDraft(document)) {
                    throw new BusinessException("Solo se pueden eliminar propuestas sin decisión registrada.");
                }
                documents.remove(i);
                repository.saveAll(documents);
                return;
            }
        }
        throw new BusinessException("No existe un documento con el id " + id + ".");
    }

    public void registerDecision(String id, Status status, LocalDate date, String act) {
        List<Document> documents = repository.findAll();
        Document document = requireDocument(documents, id);
        validateDocument(document);
        document.registerDecision(status, date, act);
        repository.saveAll(documents);
    }

    // ---------- Consultas de vencimiento y alertas ----------

    public List<Document> getDeadlineBoard(LocalDate today) {
        validateReferenceDate(today);
        return orderDegreeWorks(repository.findAll(), today);
    }

    public List<Alert> getAlerts(LocalDate today) {
        validateReferenceDate(today);
        List<Document> documents = repository.findAll();
        List<Alert> alerts = new ArrayList<>();

        for (Document document : orderDegreeWorks(documents, today)) {
            DegreeWork work = document.getDegreeWork();
            long remaining = work.getRemainingDays(today);
            LocalDate deadline = work.getDeadline();
            DeadlineStatus status = work.getDeadlineStatus(today);

            if (status == DeadlineStatus.VENCIDO) {
                alerts.add(new Alert(AlertType.TRABAJO_VENCIDO,
                        "«" + document.getTitle() + "» venció hace " + Math.abs(remaining) + " día(s).",
                        document.getId(), deadline));
            } else if (status == DeadlineStatus.POR_VENCER) {
                AlertType type;
                if (!today.isBefore(deadline.minusDays(15))) {
                    type = AlertType.TRABAJO_VENCE_15_DIAS;
                } else if (!today.isBefore(deadline.minusMonths(1))) {
                    type = AlertType.TRABAJO_VENCE_1_MES;
                } else {
                    type = AlertType.TRABAJO_VENCE_2_MESES;
                }
                String message = remaining == 0
                        ? "«" + document.getTitle() + "» vence hoy."
                        : "«" + document.getTitle() + "» vence en " + remaining + " día(s).";
                alerts.add(new Alert(type, message, document.getId(), deadline));
            }
        }

        // El Comité tiene 15 días desde la radicación para decidir una propuesta.
        for (Document document : documents) {
            if (document == null || document.getStage() != Stage.PROPUESTA
                    || document.getStatus() != Status.EN_REVISION
                    || document.getDecisionDate() != null
                    || document.getFilingDate() == null) {
                continue;
            }
            long remaining = document.getReviewRemainingDays(today);
            if (remaining < 0) {
                alerts.add(new Alert(AlertType.PROPUESTA_DECISION_VENCIDA,
                        "La decisión de la propuesta «" + document.getTitle()
                                + "» lleva " + Math.abs(remaining) + " día(s) vencida.",
                        document.getId(), document.getReviewDueDate()));
            } else if (remaining <= PROPOSAL_WARNING_DAYS) {
                String message = remaining == 0
                        ? "La decisión de la propuesta «" + document.getTitle() + "» vence hoy."
                        : "La decisión de la propuesta «" + document.getTitle()
                        + "» vence en " + remaining + " día(s).";
                alerts.add(new Alert(AlertType.PROPUESTA_DECISION_PROXIMA,
                        message, document.getId(), document.getReviewDueDate()));
            }
        }
        return alerts;
    }

    private List<Document> orderDegreeWorks(List<Document> documents, LocalDate today) {
        List<Document> board = new ArrayList<>();
        for (Document document : documents) {
            if (document != null && document.getStage() == Stage.TRABAJO_DE_GRADO
                    && document.getDegreeWork() != null
                    && document.getDegreeWork().getApprovalDate() != null) {
                board.add(document);
            }
        }
        for (int i = 0; i < board.size(); i++) {
            for (int j = i + 1; j < board.size(); j++) {
                long first = board.get(i).getDegreeWork().getRemainingDays(today);
                long second = board.get(j).getDegreeWork().getRemainingDays(today);
                if (second < first) {
                    Document current = board.get(i);
                    board.set(i, board.get(j));
                    board.set(j, current);
                }
            }
        }
        return board;
    }

    private void validateReferenceDate(LocalDate today) {
        if (today == null) {
            throw new BusinessException("Debe indicar la fecha de consulta.");
        }
    }

    // ---------- Seguimiento del trabajo de grado (después de la aprobación) ----------

    public void assignJuries(String id, String act, List<Jury> juries) {
        if (act == null || act.trim().isEmpty()) {
            throw new BusinessException("El acta de envío al jurado es obligatoria.");
        }
        List<Document> documents = repository.findAll();
        Document document = requireDegreeWork(documents, id);
        DegreeWork work = document.getDegreeWork();
        work.replaceJuries(juries);
        work.setJuryAssignmentAct(act.trim());
        repository.saveAll(documents);
    }

    public void updateObservations(String id, String observations) {
        List<Document> documents = repository.findAll();
        Document document = requireDegreeWork(documents, id);
        String text = observations == null ? null : observations.trim();
        document.getDegreeWork().setObservations(text);
        repository.saveAll(documents);
    }

    public void addAttachment(String id, Attachment attachment) {
        if (attachment == null || attachment.getPath() == null
                || attachment.getPath().trim().isEmpty()) {
            throw new BusinessException("El documento adjunto debe tener una ruta.");
        }
        List<Document> documents = repository.findAll();
        Document document = requireDegreeWork(documents, id);
        if (document.getAttachments().contains(attachment)) {
            throw new BusinessException("Ya existe un adjunto con la ruta " + attachment.getPath() + ".");
        }
        document.addAttachment(attachment);
        repository.saveAll(documents);
    }

    private Document requireDegreeWork(List<Document> documents, String id) {
        Document document = requireDocument(documents, id);
        if (!isDegreeWork(document)) {
            throw new BusinessException("El documento " + id + " aún no es un trabajo de grado aprobado.");
        }
        return document;
    }

    // ---------- Utilidades ----------

    private Document requireDocument(List<Document> documents, String id) {
        validateDocumentId(id);
        for (Document document : documents) {
            if (id.equals(document.getId())) {
                return document;
            }
        }
        throw new BusinessException("No existe un documento con el id " + id + ".");
    }

    private void validateDocumentId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new BusinessException("Debe indicar el identificador del expediente.");
        }
    }

    private boolean isDraft(Document document) {
        return document != null && document.getStage() == Stage.PROPUESTA
                && document.getStatus() == Status.EN_REVISION
                && document.getDecisionDate() == null
                && document.getDegreeWork() == null
                && document.getStatusAct() == null;
    }

    private boolean isDegreeWork(Document document) {
        return document != null && document.getStage() == Stage.TRABAJO_DE_GRADO
                && document.getDegreeWork() != null;
    }

    private void validateDocument(Document document) {
        if (document == null) {
            throw new BusinessException("El documento no puede ser nulo.");
        }
        validateDocumentId(document.getId());
        if (document.getTitle() == null || document.getTitle().trim().isEmpty()) {
            throw new BusinessException("El título es obligatorio.");
        }
        if (document.getModality() == null) {
            throw new BusinessException("La modalidad es obligatoria.");
        }
        if (document.getFilingDate() == null) {
            throw new BusinessException("La fecha de radicación es obligatoria.");
        }
        List<String> studentIds = document.getStudentIds();
        if (studentIds == null || studentIds.isEmpty()) {
            throw new BusinessException("Debe indicar al menos un estudiante.");
        }
        if (studentIds.size() > Document.MAX_AUTHORS) {
            throw new BusinessException("Un trabajo de grado no puede tener más de "
                    + Document.MAX_AUTHORS + " autores.");
        }
        for (int i = 0; i < studentIds.size(); i++) {
            String code = studentIds.get(i);
            if (code == null || code.trim().isEmpty()) {
                throw new BusinessException("Hay un código de estudiante vacío.");
            }
            if (studentService.buscarPorCodigo(code) == null) {
                throw new BusinessException("No existe un estudiante con el código " + code + ".");
            }
            for (int j = i + 1; j < studentIds.size(); j++) {
                if (code.equals(studentIds.get(j))) {
                    throw new BusinessException("El estudiante " + code + " está repetido.");
                }
            }
        }
        String directorId = document.getDirectorId();
        if (directorId == null || directorId.trim().isEmpty()) {
            throw new BusinessException("El director es obligatorio.");
        }
        if (teacherService.buscarPorId(directorId) == null) {
            throw new BusinessException("No existe un docente con el id " + directorId + " (director).");
        }
        String codirectorId = document.getCodirectorId();
        if (codirectorId != null && !codirectorId.trim().isEmpty()) {
            if (codirectorId.equals(directorId)) {
                throw new BusinessException("El director y el codirector no pueden ser la misma persona.");
            }
            if (teacherService.buscarPorId(codirectorId) == null) {
                throw new BusinessException("No existe un docente con el id " + codirectorId + " (codirector).");
            }
        }
        if (document.getCompany() != null
                && document.getModality() != Modality.PRACTICA_EMPRESARIAL_O_SOCIAL) {
            throw new BusinessException("Solo la práctica empresarial o social puede tener empresa.");
        }
    }
}