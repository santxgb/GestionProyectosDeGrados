package co.edu.uptc.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import co.edu.uptc.model.Alert;
import co.edu.uptc.model.DeadlineEntry;
import co.edu.uptc.model.DegreeWork;
import co.edu.uptc.model.Document;
import co.edu.uptc.model.enums.AlertType;
import co.edu.uptc.model.enums.DeadlineStatus;
import co.edu.uptc.model.enums.Stage;

public class DeadlineService {

    private final DocumentService documentService;

    public DeadlineService(DocumentService documentService) {
        this.documentService = documentService;
    }

    // Trabajos de grado con fecha de aprobación, del más urgente al menos urgente.
    public List<DeadlineEntry> getBoard(LocalDate today) {
        List<DeadlineEntry> board = new ArrayList<>();
        for (Document document : documentService.listar()) {
            DegreeWork work = document.getDegreeWork();
            if (document.getStage() != Stage.TRABAJO_DE_GRADO
                    || work == null || work.getApprovalDate() == null) {
                continue;
            }
            board.add(new DeadlineEntry(document, work.getDeadline(),
                    work.getRemainingDays(today), work.getDeadlineStatus(today)));
        }
        board.sort(Comparator.comparingLong(DeadlineEntry::getRemainingDays));
        return board;
    }

    // Solo los vencidos y los por vencer, en el mismo orden del tablero.
    public List<Alert> getAlerts(LocalDate today) {
        List<Alert> alerts = new ArrayList<>();
        for (DeadlineEntry entry : getBoard(today)) {
            Document document = entry.getDocument();
            long days = entry.getRemainingDays();
            if (entry.getStatus() == DeadlineStatus.VENCIDO) {
                alerts.add(new Alert(AlertType.TRABAJO_VENCIDO,
                        "«" + document.getTitle() + "» venció hace " + Math.abs(days) + " día(s).",
                        document.getId(), entry.getDeadline()));
            } else if (entry.getStatus() == DeadlineStatus.POR_VENCER) {
                alerts.add(new Alert(AlertType.TRABAJO_POR_VENCER,
                        "«" + document.getTitle() + "» vence en " + days + " día(s).",
                        document.getId(), entry.getDeadline()));
            }
        }
        return alerts;
    }
}