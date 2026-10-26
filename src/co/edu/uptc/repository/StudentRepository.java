package co.edu.uptc.repository;

import co.edu.uptc.model.Student;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class StudentRepository {

    private List<Student> students;
    private String filePath;

    public StudentRepository(String filePath) {
        this.filePath = filePath;
        this.students = new ArrayList<>();
        load();
    }

    public void add(Student item) {
        students.add(item);
        save();
    }

    public List<Student> getAll() {
        return new ArrayList<>(students);
    }

    public Student findById(String id) {
        for (Student student : students) {
            if (student.getCode().equals(id)) {
                return student;
            }
        }
        return null;
    }

    public void update(Student item) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getCode().equals(item.getCode())) {
                students.set(i, item);
                save();
                return;
            }
        }
    }

    public void delete(String id) {
        Student student = findById(id);
        if (student != null) {
            students.remove(student);
            save();
        }
    }

    private void save() {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(students);
        } catch (IOException e) {
            System.out.println("Error al guardar los estudiantes: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void load() {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            students = (List<Student>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al cargar los estudiantes: " + e.getMessage());
        }
    }
}