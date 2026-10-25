package co.edu.uptc.model.enums;

public enum Status {

    PENDING("Pendiente"),
    APPROVED("Aprobada"),
    APPROVED_WITH_CHANGES("Aprobada con modificaciones"),
    REJECTED("No aprobada");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
