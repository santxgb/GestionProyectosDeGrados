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
import co.edu.uptc.model.enums.Status;

public class DeadlineService {

    // Margen provisional del equipo; los 15 días de revisión sí son la regla confirmada.
    private static final int PROPOSAL_WARNING_DAYS = 3;

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
                AlertType type;
                if (!today.isBefore(entry.getDeadline().minusDays(15))) {
                    type = AlertType.TRABAJO_VENCE_15_DIAS;
                } else if (!today.isBefore(entry.getDeadline().minusMonths(1))) {
                    type = AlertType.TRABAJO_VENCE_1_MES;
                } else {
                    type = AlertType.TRABAJO_VENCE_2_MESES;
                }
                String message = days == 0
                        ? "«" + document.getTitle() + "» vence hoy."
                        : "«" + document.getTitle() + "» vence en " + days + " día(s).";
                alerts.add(new Alert(type, message, document.getId(), entry.getDeadline()));
            }
        }
        // Propuestas pendientes: el Comité cuenta con 15 días desde la radicación.
        // No se generan avisos para propuestas con decisión registrada.
        for (Document document : documentService.listar()) {
            if (document == null || document.getStage() != Stage.PROPUESTA
                    || document.getStatus() != Status.EN_REVISION
                    || document.getDecisionDate() != null
                    || document.getFilingDate() == null) {
                continue;
            }

            long remaining = document.getReviewRemainingDays(today);
            if (remaining < 0) {
                alerts.add(new Alert(AlertType.PROPUESTA_DECISION_VENCIDA,
                        "La decisión de la propuesta «" + document.getTitle()
                                + "» lleva " + Math.abs(remaining) + " día(s) vencida.",
                        document.getId(), document.getReviewDueDate()));
            } else if (remaining <= PROPOSAL_WARNING_DAYS) {
                String message = remaining == 0
                        ? "La decisión de la propuesta «" + document.getTitle() + "» vence hoy."
                        : "La decisión de la propuesta «" + document.getTitle()
                        + "» vence en " + remaining + " día(s).";
                alerts.add(new Alert(AlertType.PROPUESTA_DECISION_PROXIMA,
                        message, document.getId(), document.getReviewDueDate()));
            }
        }
        return alerts;
    }
}