package co.edu.uptc.controller;

import co.edu.uptc.model.Document;
import co.edu.uptc.model.enums.Status;
import co.edu.uptc.service.DocumentService;

import java.time.LocalDate;
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

    public boolean registrarDocumento(Document document) {
        return documentService.save(document);
    }

    public boolean actualizarDocumento(Document document) {
        return documentService.update(document);
    }

    public boolean eliminarDocumento(String id) {
        return documentService.delete(id);
    }

    public boolean registrarDecisionDocumento(String id, Status status, LocalDate date, String act) {
        return documentService.registerDecision(id, status, date, act);
    }
}
