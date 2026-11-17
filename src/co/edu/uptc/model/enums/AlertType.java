package co.edu.uptc.model.enums;

public enum AlertType {
    PROPUESTA_DECISION_PROXIMA("Decisión de la propuesta próxima a vencer"),
    PROPUESTA_DECISION_VENCIDA("Decisión de la propuesta vencida"),
    PROPUESTA_APROBADA("Propuesta aprobada"),
    PROPUESTA_APROBADA_CON_MODIFICACIONES("Propuesta aprobada con modificaciones"),
    PROPUESTA_RECHAZADA("Propuesta rechazada"),
    TRABAJO_VENCE_2_MESES("El trabajo de grado vence en 2 meses"),
    TRABAJO_VENCE_1_MES("El trabajo de grado vence en 1 mes"),
    TRABAJO_VENCE_15_DIAS("El trabajo de grado vence en 15 días"),
    TRABAJO_VENCIDO("Trabajo de grado vencido");

    private final String displayName;

    AlertType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
