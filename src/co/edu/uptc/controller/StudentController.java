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

    public boolean registrarEstudiante(Student student) {
        return studentService.save(student);
    }

    public boolean actualizarEstudiante(Student student) {
        return studentService.update(student);
    }

    public boolean eliminarEstudiante(String code) {
        return studentService.delete(code);
    }
}
