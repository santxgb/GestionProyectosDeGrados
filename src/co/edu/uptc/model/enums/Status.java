package co.edu.uptc.model.enums;

public enum Status {

    EN_REVISION("En revisión"),
    APROBADO("Aprobado"),
    APROBADO_CON_MODIFICACIONES("Aprobado con modificaciones"),
    NO_APROBADO("No aprobado");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}