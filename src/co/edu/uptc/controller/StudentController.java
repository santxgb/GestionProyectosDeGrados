package co.edu.uptc.controller;

import co.edu.uptc.model.Student;
import co.edu.uptc.service.StudentService;

import java.util.List;

public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    public List<Student> listarEstudiantes() {
        return studentService.listar();
    }

    public Student buscarEstudiantePorId(String id) {
        return studentService.buscarPorId(id);
    }
}
