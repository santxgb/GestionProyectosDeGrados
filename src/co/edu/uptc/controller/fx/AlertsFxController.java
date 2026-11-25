package co.edu.uptc.controller.fx;

import co.edu.uptc.controller.DocumentController;
import co.edu.uptc.model.Alert;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

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

    /** Exporta con el método de negocio existente, sin generar el contenido desde la vista. */
    @FXML
    private void onExportTxt() {
        if (documentController == null) {
            return;
        }

        try {
            if (documentController.obtenerAlertas().isEmpty()) {
                statusLabel.setText("No hay alertas pendientes para exportar.");
                showMessage(javafx.scene.control.Alert.AlertType.INFORMATION,
                        "Exportar alertas", "No hay alertas pendientes para exportar.");
                return;
            }

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Guardar reporte de alertas");
            chooser.setInitialFileName("alertas_" + LocalDate.now() + ".txt");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"));

            File file = chooser.showSaveDialog(closeButton.getScene().getWindow());
            if (file == null) {
                statusLabel.setText("Exportación cancelada.");
                return;
            }
            if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }

            // Comprobar el nombre definitivo, después de agregar la extensión si falta.
            if (file.exists() && !confirmOverwrite(file)) {
                statusLabel.setText("Exportación cancelada. El archivo existente no se modificó.");
                return;
            }

            documentController.exportarAlertasTxt(file);
            statusLabel.setText("Alertas exportadas en: " + file.getAbsolutePath());
            showMessage(javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Exportar alertas", "Reporte guardado en:\n" + file.getAbsolutePath());
        } catch (IOException exception) {
            statusLabel.setText("No se pudo guardar el reporte: " + exception.getMessage());
            showMessage(javafx.scene.control.Alert.AlertType.ERROR,
                    "Error al exportar", "No se pudo guardar el archivo: " + exception.getMessage());
        } catch (RuntimeException exception) {
            statusLabel.setText("No se pudieron exportar las alertas: " + exception.getMessage());
            showMessage(javafx.scene.control.Alert.AlertType.ERROR,
                    "Error al exportar", exception.getMessage());
        }
    }

    private boolean confirmOverwrite(File file) {
        javafx.scene.control.Alert dialog = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Confirmar reemplazo");
        dialog.setHeaderText("El archivo ya existe");
        dialog.setContentText(file.getAbsolutePath() + "\n¿Deseas reemplazarlo?");
        dialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        return dialog.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void showMessage(javafx.scene.control.Alert.AlertType type,
                             String title, String content) {
        javafx.scene.control.Alert dialog = new javafx.scene.control.Alert(type);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(content);
        dialog.showAndWait();
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
