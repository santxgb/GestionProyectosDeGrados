package co.edu.uptc.model.enums;

public enum Modality {

    MONOGRAPH("Monografía"),
    RESEARCH_PROJECT("Proyecto de Investigación"),
    RESEARCH_GROUP_PARTICIPATION("Participación activa en Grupo de Investigación"),
    BUSINESS_OR_SOCIAL_INTERNSHIP("Práctica con Proyección Empresarial o Social"),
    POSTGRADUATE_SEMESTER("Primer semestre de posgrado");

    private final String displayName;

    Modality(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
