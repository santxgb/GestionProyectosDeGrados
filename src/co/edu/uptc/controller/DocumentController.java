package co.edu.uptc.controller;

import co.edu.uptc.model.Document;
import co.edu.uptc.service.DocumentService;

import java.util.List;

public class DocumentController {

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }
    public List<Document> listarDocumentos() {
        return documentService.listar();
    }
    public Document buscarDocumentoPorId(String id) {
        Document document = documentService.buscarPorId(id);
        if (document == null) {
            return null; 
        }
        return document;
    }
}
