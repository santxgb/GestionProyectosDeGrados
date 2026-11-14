package co.edu.uptc.controller;

import co.edu.uptc.model.Teacher;
import co.edu.uptc.service.TeacherService;

import java.util.List;

public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    public List<Teacher> listarProfesores() {
        return teacherService.listar();
    }

    public Teacher buscarProfesorPorId(String id) {
        return teacherService.buscarPorId(id);
    }

    public boolean registrarDocente(Teacher teacher) {
        return teacherService.save(teacher);
    }

    public boolean actualizarDocente(Teacher teacher) {
        return teacherService.update(teacher);
    }

    public boolean eliminarDocente(String id) {
        return teacherService.delete(id);
    }
}
