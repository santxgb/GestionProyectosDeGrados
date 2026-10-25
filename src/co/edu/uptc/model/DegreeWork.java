package co.edu.uptc.model;

import co.edu.uptc.model.enums.DeadlineStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DegreeWork implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int MIN_MONTHS = 4;
    private static final int MAX_MONTHS = 12;
    private static final int WARNING_MONTHS = 2;

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
        return juries;
    }

    public void addJury(Jury jury) {
        juries.add(jury);
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
        dates.add(getDeadline().minusMonths(WARNING_MONTHS));
        dates.add(getDeadline().minusMonths(1));
        dates.add(getDeadline().minusDays(15));
        return dates;
    }
}