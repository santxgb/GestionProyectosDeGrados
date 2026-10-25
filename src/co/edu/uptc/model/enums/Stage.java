package co.edu.uptc.model.enums;

public enum Stage {

    PROPUESTA("Propuesta"),
    TRABAJO_DE_GRADO("Trabajo de grado");

    private final String displayName;

    Stage(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}