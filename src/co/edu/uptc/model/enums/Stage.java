package co.edu.uptc.model.enums;

public enum Stage {

    PROPOSAL("Propuesta"),
    DEGREE_WORK("Trabajo de grado");

    private final String displayName;

    Stage(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
