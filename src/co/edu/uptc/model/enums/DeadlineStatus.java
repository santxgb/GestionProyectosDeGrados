package co.edu.uptc.model.enums;

public enum DeadlineStatus {

    PLAZO_MINIMO_PENDIENTE("Plazo mínimo pendiente"),
    VENTANA_DE_PRESENTACION("Ventana de presentación"),
    POR_VENCER("Por vencer"),
    VENCIDO("Vencido");

    private final String displayName;

    DeadlineStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
