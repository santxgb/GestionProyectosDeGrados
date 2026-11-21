package co.edu.uptc.main;

import co.edu.uptc.controller.DocumentController;
import co.edu.uptc.controller.StudentController;
import co.edu.uptc.controller.TeacherController;
import co.edu.uptc.service.DocumentService;
import co.edu.uptc.service.StudentService;
import co.edu.uptc.service.TeacherService;
import co.edu.uptc.view.MainView;

public class Main {

    public static void main(String[] args) {
        createMainView().setVisible(true);
    }

    public static MainView createMainView() {
        DocumentService documentService = new DocumentService();
        DocumentController documentController = new DocumentController(documentService);

        StudentService studentService = new StudentService();
        StudentController studentController = new StudentController(studentService);

        TeacherService teacherService = new TeacherService();
        TeacherController teacherController = new TeacherController(teacherService);

        return new MainView(documentController, studentController, teacherController);
    }
}
