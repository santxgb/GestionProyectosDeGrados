package co.edu.uptc.controller;

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
