package co.edu.uptc.controller;

import co.edu.uptc.model.Student;
import co.edu.uptc.service.StudentService;

import java.util.List;

public class StudentController {

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
    public List<Student> listarEstudiantes() {
        return studentService.listar();
    }
    public Student buscarEstudiantePorId(String id) {
       Student student = studentService.buscarPorId(id);
        if (student == null) {
            return null; 
        }
        return student;
    }
}
