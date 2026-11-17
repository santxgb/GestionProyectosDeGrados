package co.edu.uptc.model.enums;

public enum AlertType {
    PROPUESTA_DECISION_PROXIMA("Decisión de la propuesta próxima a vencer"),
    PROPUESTA_DECISION_VENCIDA("Decisión de la propuesta vencida"),
    PROPUESTA_APROBADA("Propuesta aprobada"),
    PROPUESTA_APROBADA_CON_MODIFICACIONES("Propuesta aprobada con modificaciones"),
    PROPUESTA_RECHAZADA("Propuesta rechazada"),
    TRABAJO_POR_VENCER("El trabajo de grado está por vencer"),
    TRABAJO_VENCIDO("Trabajo de grado vencido");

    private final String displayName;

    AlertType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
