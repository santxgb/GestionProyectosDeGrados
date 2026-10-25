package co.edu.uptc.model;

import java.io.Serializable;
import java.util.Objects;

public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idNumber;
    private String code;
    private String lastNames;
    private String firstNames;
    private String email;
    private String phone;

    public Student() {
    }

    public Student(String idNumber, String code, String lastNames, String firstNames, String email, String phone) {
        this.idNumber = idNumber;
        this.code = code;
        this.lastNames = lastNames;
        this.firstNames = firstNames;
        this.email = email;
        this.phone = phone;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        Student student = (Student) o;
        return Objects.equals(idNumber, student.idNumber) && Objects.equals(code, student.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idNumber, code);
    }
}
