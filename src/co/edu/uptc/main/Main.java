package co.edu.uptc.main;

import co.edu.uptc.controller.DocumentController;
import co.edu.uptc.controller.StudentController;
import co.edu.uptc.service.DocumentService;
import co.edu.uptc.service.StudentService;
import co.edu.uptc.view.MainView;

public class Main {

    public static void main(String[] args) {
        DocumentService documentService = new DocumentService();
        DocumentController documentController = new DocumentController(documentService);

        StudentService studentService = new StudentService();
        StudentController studentController = new StudentController(studentService);

        MainView mainView = new MainView(documentController, studentController);
        mainView.setVisible(true);
    }
}
