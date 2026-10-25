package co.edu.uptc.model;

import java.io.Serializable;
import java.util.Objects;

public class Jury implements Serializable {
    private String id;
    private String lastNames;
    private String firstNames;
    private String email;
    private String phone;
    private boolean external;
    private String institution;

    public Jury() {
    }

    public Jury(String id, String lastNames, String firstNames, String email, String phone, boolean external, String institution) {
        this.id = id;
        this.lastNames = lastNames;
        this.firstNames = firstNames;
        this.email = email;
        this.phone = phone;
        this.external = external;
        this.institution = institution;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastNames() {
        return lastNames;
    }

    public void setLastNames(String lastNames) {
        this.lastNames = lastNames;
    }

    public String getFirstNames() {
        return firstNames;
    }

    public void setFirstNames(String firstNames) {
        this.firstNames = firstNames;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getFullName() {
        return lastNames + ", " + firstNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Jury jury = (Jury) o;
        return Objects.equals(id, jury.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
