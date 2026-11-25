package co.edu.uptc.controller.fx;

import co.edu.uptc.controller.DocumentController;
import co.edu.uptc.model.Document;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.List;

/**
 * Vista JavaFX para consultar las observaciones de la propuesta y actualizar
 * únicamente las observaciones de seguimiento del trabajo de grado.
 */
public class ObservationsFxController {

    @FXML private TableView<Document> workTable;
    @FXML private TableColumn<Document, String> idColumn;
    @FXML private TableColumn<Document, String> titleColumn;
    @FXML private TableColumn<Document, String> approvalDateColumn;
    @FXML private TableColumn<Document, String> deadlineColumn;
    @FXML private Label summaryLabel;
    @FXML private Label selectedWorkLabel;
    @FXML private Label statusLabel;
    @FXML private TextArea proposalObservationsArea;
    @FXML private TextArea followUpObservationsArea;
    @FXML private Button saveButton;
    @FXML private Button restoreButton;
    @FXML private Button closeButton;

    private DocumentController documentController;
    private String savedFollowUp = "";
    private boolean changingSelection;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(safe(cell.getValue().getId())));
        titleColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(safe(cell.getValue().getTitle())));
        approvalDateColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getDegreeWork()
                        .getApprovalDate().toString()));
        deadlineColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getDegreeWork()
                        .getDeadline().toString()));

        for (TableColumn<Document, ?> column : workTable.getColumns()) {
            column.setSortable(false); // Se conserva el orden de vencimiento del servicio.
        }
        workTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, previous, selected) -> onSelectionChanged(previous, selected));
        clearForm();
    }

    public void setDocumentController(DocumentController documentController) {
        this.documentController = documentController;
        reloadWorks(null);
    }

    private void onSelectionChanged(Document previous, Document selected) {
        if (changingSelection) {
            return;
        }
        if (previous != null && hasUnsavedChanges() && !confirmDiscard()) {
            changingSelection = true;
            workTable.getSelectionModel().select(previous);
            changingSelection = false;
            return;
        }
        showWork(selected);
    }

    private void showWork(Document document) {
        if (document == null || document.getDegreeWork() == null) {
            clearForm();
            return;
        }
        selectedWorkLabel.setText("Expediente: " + safe(document.getId())
                + "  |  " + safe(document.getTitle()));
        String initial = document.getObservations();
        proposalObservationsArea.setText(initial == null || initial.isBlank()
                ? "Sin observaciones iniciales registradas." : initial);
        savedFollowUp = document.getDegreeWork().getObservations() == null
                ? "" : document.getDegreeWork().getObservations();
        followUpObservationsArea.setText(savedFollowUp);
        followUpObservationsArea.setDisable(false);
        saveButton.setDisable(false);
        restoreButton.setDisable(false);
        statusLabel.setText("Puedes actualizar las observaciones de seguimiento del expediente "
                + document.getId() + ".");
    }

    private void clearForm() {
        selectedWorkLabel.setText("Selecciona un trabajo de grado aprobado en la tabla.");
        proposalObservationsArea.clear();
        followUpObservationsArea.clear();
        followUpObservationsArea.setDisable(true);
        saveButton.setDisable(true);
        restoreButton.setDisable(true);
        savedFollowUp = "";
    }

    @FXML
    private void onRefresh() {
        if (documentController == null) {
            return;
        }
        if (hasUnsavedChanges() && !confirmDiscard()) {
            return;
        }
        Document selected = workTable.getSelectionModel().getSelectedItem();
        reloadWorks(selected == null ? null : selected.getId());
    }

    private void reloadWorks(String selectedId) {
        if (documentController == null) {
            return;
        }
        try {
            List<Document> works = documentController.obtenerTablero();
            changingSelection = true;
            workTable.getSelectionModel().clearSelection();
            workTable.getItems().setAll(works);
            changingSelection = false;
            clearForm();
            summaryLabel.setText("Trabajos aprobados: " + works.size()
                    + "  |  Ordenados por proximidad de vencimiento");
            statusLabel.setText("Selecciona un trabajo para consultar sus observaciones.");
            if (selectedId != null) {
                for (Document work : works) {
                    if (selectedId.equals(work.getId())) {
                        workTable.getSelectionModel().select(work);
                        workTable.scrollTo(work);
                        break;
                    }
                }
            }
        } catch (RuntimeException exception) {
            changingSelection = false;
            showError("No se pudieron consultar los trabajos aprobados", exception);
        }
    }

    @FXML
    private void onRestore() {
        if (workTable.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        followUpObservationsArea.setText(savedFollowUp);
        statusLabel.setText("Texto guardado restaurado en el formulario; no se modificó el JSON.");
    }

    @FXML
    private void onSaveObservations() {
        Document selected = workTable.getSelectionModel().getSelectedItem();
        if (selected == null || documentController == null) {
            showInformation("Primero selecciona un trabajo de grado aprobado.");
            return;
        }
        if (!hasUnsavedChanges()) {
            showInformation("No hay cambios de seguimiento para guardar.");
            return;
        }
        String observations = followUpObservationsArea.getText();
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar actualización de seguimiento");
        confirmation.setHeaderText("Expediente " + selected.getId());
        confirmation.setContentText(observations == null || observations.isBlank()
                ? "Las observaciones de seguimiento quedarán vacías. "
                + "Las observaciones iniciales de la propuesta se conservarán. ¿Continuar?"
                : "Se reemplazará el texto actual de seguimiento. "
                + "Las observaciones iniciales de la propuesta no cambiarán. ¿Continuar?");
        confirmation.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        if (confirmation.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
            statusLabel.setText("Actualización cancelada. No se guardaron cambios.");
            return;
        }
        try {
            String id = selected.getId();
            documentController.actualizarObservaciones(id, observations);
            savedFollowUp = observations == null ? "" : observations.trim();
            reloadWorks(id);
            statusLabel.setText("Observaciones de seguimiento guardadas para el expediente " + id + ".");
        } catch (RuntimeException exception) {
            showError("No se pudieron guardar las observaciones de seguimiento", exception);
        }
    }

    @FXML
    private void onClose() {
        if (hasUnsavedChanges() && !confirmDiscard()) {
            return;
        }
        ((Stage) closeButton.getScene().getWindow()).close();
    }

    private boolean hasUnsavedChanges() {
        return workTable.getSelectionModel().getSelectedItem() != null
                && !savedFollowUp.equals(followUpObservationsArea.getText());
    }

    private boolean confirmDiscard() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Cambios sin guardar");
        confirmation.setHeaderText("Hay observaciones de seguimiento sin guardar");
        confirmation.setContentText("¿Deseas descartarlas y continuar?");
        confirmation.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        return confirmation.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void showInformation(String message) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Observaciones de seguimiento");
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        dialog.showAndWait();
    }

    private void showError(String heading, RuntimeException exception) {
        statusLabel.setText(heading + ".");
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle("Error en observaciones");
        dialog.setHeaderText(heading);
        dialog.setContentText(exception.getMessage());
        dialog.showAndWait();
    }

    private String safe(String text) {
        return text == null || text.isBlank() ? "-" : text;
    }
}
