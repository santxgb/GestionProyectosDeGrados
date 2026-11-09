package co.edu.uptc.controller;

import co.edu.uptc.model.Teacher;
import co.edu.uptc.service.TeacherService;

import java.util.List;

public class TeacherController {

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }
    public List<Teacher> listarProfesores() {
        return teacherService.listar();
    }
    publicTeacher buscarProfesorPorId(String id) {
        Teacher teacher = dteacherService.buscarPorId(id);
        if (teacher == null) {
            return null; 
        }
        return teacher;
    }
        
}
