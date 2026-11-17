package co.edu.uptc.controller;

import co.edu.uptc.model.Alert;
import co.edu.uptc.model.DeadlineEntry;
import co.edu.uptc.service.DeadlineService;
import java.time.LocalDate;
import java.util.List;

public class DeadlineController {

    private final DeadlineService deadlineService;

    public DeadlineController(DeadlineService deadlineService) {
        this.deadlineService = deadlineService;
    }

    public List<DeadlineEntry> obtenerTablero() {
        return deadlineService.getBoard(LocalDate.now());
    }

    public List<Alert> obtenerAlertas() {
        return deadlineService.getAlerts(LocalDate.now());
    }
}