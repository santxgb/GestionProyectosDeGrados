package co.edu.uptc.main;

import co.edu.uptc.controller.DocumentController;
import co.edu.uptc.service.DocumentService;
import co.edu.uptc.view.MainView;

public class Main {

    public static void main(String[] args) {
        DocumentService documentService = new DocumentService();
        DocumentController documentController = new DocumentController(documentService);

        MainView mainView = new MainView(documentController);
        mainView.setVisible(true);
    }
}
