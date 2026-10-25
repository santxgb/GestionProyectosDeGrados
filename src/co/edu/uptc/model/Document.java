package co.edu.uptc.model;

import co.edu.uptc.model.enums.Modality;
import co.edu.uptc.model.enums.Stage;
import co.edu.uptc.model.enums.Status;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Document implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int REVIEW_DAYS = 15;

    private String id;
    private String title;
    private Modality modality;
    private Stage stage;
    private List<String> studentIds;
    private String directorId;
    private String codirectorId;
    private Company company;
    private Jury reviewer;
    private LocalDate filingDate;
    private LocalDate decisionDate;
    private Status status;
    private String statusAct;
    private String observations;
    private List<Attachment> attachments;
    private DegreeWork degreeWork;

    public Document(String id, String title, Modality modality, List<String> studentIds, String directorId, LocalDate filingDate) {
        this.id = id;
        this.title = title;
        this.modality = modality;
        this.studentIds = studentIds;
        this.directorId = directorId;
        this.filingDate = filingDate;
        this.stage = Stage.PROPUESTA;
        this.status = Status.EN_REVISION;
        this.attachments = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Modality getModality() {
        return modality;
    }

    public void setModality(Modality modality) {
        this.modality = modality;
    }

    public Stage getStage() {
        return stage;
    }

    public List<String> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<String> studentIds) {
        this.studentIds = studentIds;
    }

    public String getDirectorId() {
        return directorId;
    }

    public void setDirectorId(String directorId) {
        this.directorId = directorId;
    }

    public String getCodirectorId() {
        return codirectorId;
    }

    public void setCodirectorId(String codirectorId) {
        this.codirectorId = codirectorId;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Jury getReviewer() {
        return reviewer;
    }

    public void setReviewer(Jury reviewer) {
        this.reviewer = reviewer;
    }

    public LocalDate getFilingDate() {
        return filingDate;
    }

    public void setFilingDate(LocalDate filingDate) {
        this.filingDate = filingDate;
    }

    public LocalDate getDecisionDate() {
        return decisionDate;
    }

    public Status getStatus() {
        return status;
    }

    public String getStatusAct() {
        return statusAct;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }

    public DegreeWork getDegreeWork() {
        return degreeWork;
    }

    public void registerDecision(Status status, LocalDate date, String act) {
        this.status = status;
        this.decisionDate = date;
        this.statusAct = act;
        if (status == Status.APROBADO || status == Status.APROBADO_CON_MODIFICACIONES) {
            this.stage = Stage.TRABAJO_DE_GRADO;
            this.degreeWork = new DegreeWork(date, act);
        }
    }

    public LocalDate getReviewDueDate() {
        return filingDate.plusDays(REVIEW_DAYS);
    }

    public long getReviewRemainingDays(LocalDate today) {
        return ChronoUnit.DAYS.between(today, getReviewDueDate());
    }

    public boolean isReviewOverdue(LocalDate today) {
        return status == Status.EN_REVISION && today.isAfter(getReviewDueDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Document document = (Document) o;
        return Objects.equals(id, document.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}