package co.edu.uptc.model;

import co.edu.uptc.model.enums.Modality;
import co.edu.uptc.model.enums.Status;

import java.time.LocalDate;
import java.util.List;

public class DegreeWork {
    private String id;
    private String title;
    private Modality modality;
    private List<Student> students;
    private Teacher director;
    private Teacher codirector;
    private Company company;
    private LocalDate filingDate;
    private LocalDate approvalDate;
    private List<Jury> juries;
    private String juryAssigmentAct;
    private Status status;
    private String statusAct;
    private String observations;
    private List<Document> documents;

    public DegreeWork() {
    }

    public DegreeWork(String id, String title, Modality modality, List<Student> students, Teacher director, Teacher codirector, Company company, LocalDate filingDate, LocalDate approvalDate, List<Jury> juries, String juryAssigmentAct, Status status, String statusAct, String observations, List<Document> documents) {
        this.id = id;
        this.title = title;
        this.modality = modality;
        this.students = students;
        this.director = director;
        this.codirector = codirector;
        this.company = company;
        this.filingDate = filingDate;
        this.approvalDate = approvalDate;
        this.juries = juries;
        this.juryAssigmentAct = juryAssigmentAct;
        this.status = status;
        this.statusAct = statusAct;
        this.observations = observations;
        this.documents = documents;
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

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Teacher getDirector() {
        return director;
    }

    public void setDirector(Teacher director) {
        this.director = director;
    }

    public Teacher getCodirector() {
        return codirector;
    }

    public void setCodirector(Teacher codirector) {
        this.codirector = codirector;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public LocalDate getFilingDate() {
        return filingDate;
    }

    public void setFilingDate(LocalDate filingDate) {
        this.filingDate = filingDate;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public List<Jury> getJuries() {
        return juries;
    }

    public void setJuries(List<Jury> juries) {
        this.juries = juries;
    }

    public String getJuryAssigmentAct() {
        return juryAssigmentAct;
    }

    public void setJuryAssigmentAct(String juryAssigmentAct) {
        this.juryAssigmentAct = juryAssigmentAct;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getStatusAct() {
        return statusAct;
    }

    public void setStatusAct(String statusAct) {
        this.statusAct = statusAct;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
