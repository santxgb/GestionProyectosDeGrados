package co.edu.uptc.model.enums;

public enum Modality {

    MONOGRAFIA("Monografía"),
    PROYECTO_DE_INVESTIGACION("Proyecto de Investigación"),
    PARTICIPACION_EN_GRUPO_DE_INVESTIGACION("Participación activa en Grupo de Investigación"),
    PRACTICA_EMPRESARIAL_O_SOCIAL("Práctica con Proyección Empresarial o Social"),
    PRIMER_SEMESTRE_DE_POSGRADO("Primer semestre de posgrado");

    private final String displayName;

    Modality(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}