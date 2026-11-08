package co.edu.uptc.repository;

import co.edu.uptc.model.Teacher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TeacherRepository {

    private List<Teacher> teachers;
    private String filePath;

    public TeacherRepository(String filePath) {
        this.filePath = filePath;
        this.teachers = new ArrayList<>();
        load();
    }

    public void add(Teacher item) {
        teachers.add(item);
        save();
    }

    public List<Teacher> getAll() {
        return new ArrayList<>(teachers);
    }

    public Teacher findById(String id) {
        for (Teacher teacher : teachers) {
            if (teacher.getId().equals(id)) {
                return teacher;
            }
        }
        return null;
    }

    public void update(Teacher item) {
        for (int i = 0; i < teachers.size(); i++) {
            if (teachers.get(i).getId().equals(item.getId())) {
                teachers.set(i, item);
                save();
                return;
            }
        }
    }

    public void delete(String id) {
        Teacher teacher = findById(id);
        if (teacher != null) {
            teachers.remove(teacher);
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
            out.writeObject(teachers);
        } catch (IOException e) {
            System.out.println("Error al guardar los docentes: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void load() {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            teachers = (List<Teacher>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al cargar los docentes: " + e.getMessage());
        }
    }
}