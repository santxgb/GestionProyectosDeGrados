package co.edu.uptc.model;

import co.edu.uptc.model.enums.AlertType;

import java.time.LocalDate;

public class Alert {

    private AlertType type;
    private String message;
    private String documentId;
    private LocalDate referenceDate;

    public Alert(AlertType type, String message, String documentId, LocalDate referenceDate) {
        this.type = type;
        this.message = message;
        this.documentId = documentId;
        this.referenceDate = referenceDate;
    }

    public AlertType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getDocumentId() {
        return documentId;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }
}