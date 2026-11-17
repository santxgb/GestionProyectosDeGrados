package co.edu.uptc.model;

import co.edu.uptc.model.enums.DeadlineStatus;
import co.edu.uptc.exception.BusinessException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DegreeWork {

    private static final int MIN_MONTHS = 4;
    private static final int MAX_MONTHS = 12;
    private static final int WARNING_MONTHS = 2;
    public static final int MAX_JURIES = 2;

    // Fecha de aprobación del Comité de Currículo (inicio del conteo de plazos).
    private LocalDate approvalDate;
    private String approvalAct;
    private List<Jury> juries;
    private String juryAssignmentAct;
    private String observations;

    public DegreeWork(LocalDate approvalDate, String approvalAct) {
        this.approvalDate = approvalDate;
        this.approvalAct = approvalAct;
        this.juries = new ArrayList<>();
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getApprovalAct() {
        return approvalAct;
    }

    public void setApprovalAct(String approvalAct) {
        this.approvalAct = approvalAct;
    }

    public List<Jury> getJuries() {
        return juries == null ? new ArrayList<>() : new ArrayList<>(juries);
    }

    public void addJury(Jury jury) {
        if (juries == null) {
            juries = new ArrayList<>();
        }
        if (juries.size() >= MAX_JURIES) {
            throw new BusinessException("Un trabajo de grado admite máximo dos jurados.");
        }
        validateJury(jury, juries);
        juries.add(jury);
    }

    public void replaceJuries(List<Jury> newJuries) {
        if (newJuries == null || newJuries.isEmpty()) {
            throw new BusinessException("Debe indicar al menos un jurado.");
        }
        if (newJuries.size() > MAX_JURIES) {
            throw new BusinessException("Un trabajo de grado admite máximo dos jurados.");
        }
        List<Jury> valid = new ArrayList<>();
        for (Jury jury : newJuries) {
            validateJury(jury, valid);
            valid.add(jury);
        }
        juries = valid;
    }

    private void validateJury(Jury jury, List<Jury> existingJuries) {
        if (jury == null || jury.getId() == null || jury.getId().trim().isEmpty()) {
            throw new BusinessException("Todos los jurados deben tener id.");
        }
        for (Jury existing : existingJuries) {
            if (jury.getId().equals(existing.getId())) {
                throw new BusinessException("No se pueden asignar jurados repetidos.");
            }
        }
    }

    public String getJuryAssignmentAct() {
        return juryAssignmentAct;
    }

    public void setJuryAssignmentAct(String juryAssignmentAct) {
        this.juryAssignmentAct = juryAssignmentAct;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public LocalDate getEarliestSubmissionDate() {
        return approvalDate.plusMonths(MIN_MONTHS);
    }

    public LocalDate getDeadline() {
        return approvalDate.plusMonths(MAX_MONTHS);
    }

    public long getRemainingDays(LocalDate today) {
        return ChronoUnit.DAYS.between(today, getDeadline());
    }

    public DeadlineStatus getDeadlineStatus(LocalDate today) {
        if (today.isAfter(getDeadline())) {
            return DeadlineStatus.VENCIDO;
        }
        if (today.isBefore(getEarliestSubmissionDate())) {
            return DeadlineStatus.PLAZO_MINIMO_PENDIENTE;
        }
        if (!today.isBefore(getDeadline().minusMonths(WARNING_MONTHS))) {
            return DeadlineStatus.POR_VENCER;
        }
        return DeadlineStatus.VENTANA_DE_PRESENTACION;
    }

    public List<LocalDate> getAlertDates() {
        List<LocalDate> dates = new ArrayList<>();
        dates.add(getDeadline().minusMonths(2));
        dates.add(getDeadline().minusMonths(1));
        dates.add(getDeadline().minusDays(15));
        return dates;
    }
}