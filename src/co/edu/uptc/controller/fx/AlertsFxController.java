package co.edu.uptc.controller.fx;

import co.edu.uptc.controller.DocumentController;
import co.edu.uptc.model.Alert;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

/** Controla la consulta de alertas; las reglas permanecen en DocumentService. */
public class AlertsFxController {

    @FXML private TableView<Alert> alertsTable;
    @FXML private TableColumn<Alert, String> typeColumn;
    @FXML private TableColumn<Alert, String> documentIdColumn;
    @FXML private TableColumn<Alert, String> referenceDateColumn;
    @FXML private TableColumn<Alert, String> messageColumn;
    @FXML private Label summaryLabel;
    @FXML private Label statusLabel;
    @FXML private TextArea detailsArea;
    @FXML private Button closeButton;

    private DocumentController documentController;

    @FXML
    private void initialize() {
        typeColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().getType() == null ? "-" : cell.getValue().getType().getDisplayName()));
        documentIdColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                safe(cell.getValue().getDocumentId())));
        referenceDateColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().getReferenceDate() == null ? "-" : cell.getValue().getReferenceDate().toString()));
        messageColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                safe(cell.getValue().getMessage())));

        // No alterar el orden establecido en DocumentService.getAlerts().
        for (TableColumn<Alert, ?> column : alertsTable.getColumns()) {
            column.setSortable(false);
        }
        alertsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, previous, selected) -> showDetails(selected));
    }

    public void setDocumentController(DocumentController documentController) {
        this.documentController = documentController;
        onRefresh();
    }

    @FXML
    private void onRefresh() {
        if (documentController == null) {
            return;
        }
        try {
            List<Alert> alerts = documentController.obtenerAlertas();
            alertsTable.getSelectionModel().clearSelection();
            alertsTable.getItems().setAll(alerts);
            detailsArea.clear();
            summaryLabel.setText("Fecha de consulta: " + LocalDate.now()
                    + "   |   Alertas pendientes: " + alerts.size());
            statusLabel.setText(alerts.isEmpty()
                    ? "No hay alertas pendientes."
                    : "Consulta actualizada. Selecciona una alerta para ver su detalle.");
        } catch (RuntimeException exception) {
            alertsTable.getItems().clear();
            detailsArea.clear();
            summaryLabel.setText("No se pudo completar la consulta.");
            statusLabel.setText("Error al consultar las alertas: " + exception.getMessage());
        }
    }

    private void showDetails(Alert alert) {
        if (alert == null) {
            detailsArea.clear();
            return;
        }
        detailsArea.setText("Tipo: "
                + (alert.getType() == null ? "-" : alert.getType().getDisplayName()) + "\n"
                + "Expediente: " + safe(alert.getDocumentId()) + "\n"
                + "Fecha límite: "
                + (alert.getReferenceDate() == null ? "-" : alert.getReferenceDate()) + "\n\n"
                + safe(alert.getMessage()));
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    @FXML
    private void onClose() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }
}
