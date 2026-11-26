package co.edu.uptc.controller;

import co.edu.uptc.model.Alert;
import co.edu.uptc.model.Attachment;
import co.edu.uptc.model.Document;
import co.edu.uptc.model.Jury;
import co.edu.uptc.model.enums.Status;
import co.edu.uptc.controller.service.DocumentService;
import java.io.File;
import java.io.IOException;
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

    public List<Document> obtenerTablero() {
        return documentService.getDeadlineBoard(LocalDate.now());
    }

    public List<Alert> obtenerAlertas() {
        return documentService.getAlerts(LocalDate.now());
    }

    public void exportarAlertasTxt(File file) throws IOException {
        documentService.exportAlertsToTxt(file, LocalDate.now());
    }

    public void registrarDocumento(Document document) {
        documentService.save(document);
    }

    public void actualizarDocumento(Document document) {
        documentService.update(document);
    }

    public void eliminarDocumento(String id) {
        documentService.delete(id);
    }

    public void registrarDecisionDocumento(String id, Status status, LocalDate date, String act) {
        documentService.registerDecision(id, status, date, act);
    }

    public void asignarJurados(String id, String act, List<Jury> juries) {
        documentService.assignJuries(id, act, juries);
    }

    public void actualizarObservaciones(String id, String observations) {
        documentService.updateObservations(id, observations);
    }

    public void agregarAnexo(String id, Attachment attachment) {
        documentService.addAttachment(id, attachment);
    }
}