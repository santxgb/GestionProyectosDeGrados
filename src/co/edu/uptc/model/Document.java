package co.edu.uptc.model;

import java.time.LocalDate;

public class Document {
    private String name;
    private String type;
    private String path;
    private LocalDate uploadDate;

    public Document() {
    }

    public Document(String name, String type, String path, LocalDate uploadDate) {
        this.name = name;
        this.type = type;
        this.path = path;
        this.uploadDate = uploadDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }
}
