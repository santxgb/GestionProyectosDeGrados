package co.edu.uptc.controller;

import co.edu.uptc.model.Document;
import co.edu.uptc.service.DocumentService;

import java.util.List;

public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    public List<Document> listarDocumentos() {
        return documentService.listar();
    }

    public Document buscarDocumentoPorId(String id) {
        return documentService.buscarPorId(id);
    }
}
