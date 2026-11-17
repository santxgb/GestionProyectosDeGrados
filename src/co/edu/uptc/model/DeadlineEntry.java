package co.edu.uptc.model;

import co.edu.uptc.model.enums.DeadlineStatus;
import java.time.LocalDate;

public class DeadlineEntry {

    private final Document document;
    private final LocalDate deadline;
    private final long remainingDays;
    private final DeadlineStatus status;

    public DeadlineEntry(Document document, LocalDate deadline, long remainingDays, DeadlineStatus status) {
        this.document = document;
        this.deadline = deadline;
        this.remainingDays = remainingDays;
        this.status = status;
    }

    public Document getDocument() {
        return document;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public long getRemainingDays() {
        return remainingDays;
    }

    public DeadlineStatus getStatus() {
        return status;
    }
}