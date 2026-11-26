package co.edu.uptc.controller;

import co.edu.uptc.model.Student;
import co.edu.uptc.controller.service.StudentService;
import java.util.List;

public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    public List<Student> listarEstudiantes() {
        return studentService.listar();
    }

    public Student buscarEstudiantePorCodigo(String code) {
        return studentService.buscarPorCodigo(code);
    }

    public void registrarEstudiante(Student student) {
        studentService.save(student);
    }

    public void actualizarEstudiante(Student student) {
        studentService.update(student);
    }

    public void eliminarEstudiante(String code) {
        studentService.delete(code);
    }
}