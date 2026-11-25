package co.edu.uptc.controller.fx;

import co.edu.uptc.controller.DocumentController;
import co.edu.uptc.model.Attachment;
import co.edu.uptc.model.Document;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class AttachmentsFxController {

    @FXML private TableView<Document> workTable;
    @FXML private TableColumn<Document, String> workIdColumn;
    @FXML private TableColumn<Document, String> workTitleColumn;
    @FXML private TableColumn<Document, String> workStatusColumn;
    @FXML private TableColumn<Document, String> workAttachmentsColumn;

    @FXML private TableView<Attachment> attachmentsTable;
    @FXML private TableColumn<Attachment, String> attachmentNameColumn;
    @FXML private TableColumn<Attachment, String> attachmentTypeColumn;
    @FXML private TableColumn<Attachment, String> attachmentDateColumn;
    @FXML private TableColumn<Attachment, String> attachmentPathColumn;

    @FXML private TextField selectedAttachmentPathField;
    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private TextField pathField;
    @FXML private DatePicker uploadDatePicker;
    @FXML private Label selectedWorkLabel;
    @FXML private Label summaryLabel;
    @FXML private Label statusLabel;
    @FXML private Button browseButton;
    @FXML private Button saveButton;
    @FXML private Button closeButton;

    private DocumentController documentController;
    private boolean changingSelection;

    @FXML
    private void initialize() {
        workIdColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(safe(cell.getValue().getId())));
        workTitleColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(safe(cell.getValue().getTitle())));
        workStatusColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getStatus() == null
                        ? "-" : cell.getValue().getStatus().getDisplayName()));
        workAttachmentsColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(String.valueOf(attachmentCount(cell.getValue()))));

        attachmentNameColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(safe(cell.getValue().getName())));
        attachmentTypeColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(safe(cell.getValue().getType())));
        attachmentDateColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getUploadDate() == null
                        ? "-" : cell.getValue().getUploadDate().toString()));
        attachmentPathColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(safe(cell.getValue().getPath())));

        for (TableColumn<Document, ?> column : workTable.getColumns()) {
            column.setSortable(false);
        }
        workTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, previous, current) -> onWorkChanged(previous, current));
        attachmentsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, previous, current) -> selectedAttachmentPathField.setText(
                        current == null ? "" : safe(current.getPath())));
        uploadDatePicker.setValue(LocalDate.now());
        showWork(null);
    }

    public void setDocumentController(DocumentController controller) {
        this.documentController = controller;
        onRefresh();
    }

    private void onWorkChanged(Document previous, Document current) {
        if (changingSelection) {
            return;
        }
        if (previous != null && hasDraft() && !confirmDiscard()) {
            changingSelection = true;
            workTable.getSelectionModel().select(previous);
            changingSelection = false;
            return;
        }
        showWork(current);
    }

    private void showWork(Document work) {
        attachmentsTable.getSelectionModel().clearSelection();
        attachmentsTable.getItems().clear();
        selectedAttachmentPathField.clear();
        clearDraft();

        boolean selected = work != null && work.getDegreeWork() != null;
        browseButton.setDisable(!selected);
        saveButton.setDisable(!selected);
        nameField.setDisable(!selected);
        typeField.setDisable(!selected);
        uploadDatePicker.setDisable(!selected);

        if (!selected) {
            selectedWorkLabel.setText("Selecciona un trabajo de grado aprobado.");
            return;
        }
        selectedWorkLabel.setText("Expediente " + safe(work.getId()) + " · "
                + safe(work.getTitle()));
        if (work.getAttachments() != null) {
            attachmentsTable.getItems().setAll(work.getAttachments());
        }
        statusLabel.setText("Anexos registrados en " + work.getId() + ": "
                + attachmentCount(work) + ". Selecciona un anexo para ver su ruta completa.");
    }

    @FXML
    private void onRefresh() {
        if (documentController == null) {
            return;
        }
        if (hasDraft() && !confirmDiscard()) {
            return;
        }
        Document previous = workTable.getSelectionModel().getSelectedItem();
        String previousId = previous == null ? null : previous.getId();
        reloadWorks(previousId);
    }

    private void reloadWorks(String selectedId) {
        try {
            List<Document> works = documentController.obtenerTablero();
            changingSelection = true;
            workTable.getSelectionModel().clearSelection();
            workTable.getItems().setAll(works);
            changingSelection = false;
            showWork(null);
            summaryLabel.setText("Trabajos aprobados: " + works.size()
                    + " · Ordenados por proximidad de vencimiento");
            statusLabel.setText("Selecciona un trabajo aprobado para consultar sus anexos.");
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
            showError("No se pudo consultar el listado de trabajos", exception);
        }
    }

    @FXML
    private void onBrowseFile() {
        if (workTable.getSelectionModel().getSelectedItem() == null) {
            showInformation("Selecciona primero un trabajo de grado aprobado.");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar archivo para registrar su ruta");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*"));
        try {
            File file = chooser.showOpenDialog(closeButton.getScene().getWindow());
            if (file == null) {
                return;
            }
            pathField.setText(file.getAbsolutePath());
            if (nameField.getText().trim().isEmpty()) {
                nameField.setText(file.getName());
            }
            statusLabel.setText("Archivo seleccionado. Se guardará solamente su ruta.");
        } catch (RuntimeException exception) {
            showError("No se pudo seleccionar el archivo", exception);
        }
    }

    @FXML
    private void onSaveAttachment() {
        Document work = workTable.getSelectionModel().getSelectedItem();
        if (documentController == null || work == null || work.getDegreeWork() == null) {
            showInformation("Selecciona un trabajo de grado aprobado.");
            return;
        }
        if (uploadDatePicker.getValue() == null) {
            showInformation("Selecciona la fecha de carga del anexo.");
            return;
        }
        Attachment attachment = new Attachment(
                nameField.getText().trim(),
                typeField.getText().trim(),
                pathField.getText().trim(),
                uploadDatePicker.getValue());

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar registro de anexo");
        confirm.setHeaderText("Expediente " + work.getId());
        confirm.setContentText("¿Registrar el anexo '" + attachment.getName()
                + "' con la ruta indicada?\n\nNo se copiará el archivo al proyecto.");
        confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
            statusLabel.setText("Registro cancelado; no se guardaron cambios.");
            return;
        }

        try {
            String id = work.getId();
            documentController.agregarAnexo(id, attachment);
            reloadWorks(id);
            statusLabel.setText("Anexo registrado correctamente en el expediente " + id + ".");
            showInformation("Anexo registrado. Recuerda conservar el archivo en su ubicación original.");
        } catch (RuntimeException exception) {
            showError("No se pudo registrar el anexo", exception);
        }
    }

    @FXML
    private void onClearDraft() {
        clearDraft();
        statusLabel.setText("Formulario limpio; no se modificaron los datos guardados.");
    }

    public void onWindowCloseRequest(WindowEvent event) {
        if (hasDraft() && !confirmDiscard()) {
            event.consume();
        }
    }

    @FXML
    private void onClose() {
        if (hasDraft() && !confirmDiscard()) {
            return;
        }
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.hide();
    }

    private void clearDraft() {
        nameField.clear();
        typeField.clear();
        pathField.clear();
        uploadDatePicker.setValue(LocalDate.now());
    }

    private boolean hasDraft() {
        return !nameField.getText().trim().isEmpty()
                || !typeField.getText().trim().isEmpty()
                || !pathField.getText().trim().isEmpty();
    }

    private boolean confirmDiscard() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Descartar formulario de anexo");
        confirm.setHeaderText("Hay datos del anexo sin guardar");
        confirm.setContentText("¿Deseas descartarlos? No se escribirá ningún dato.");
        confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        return confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private int attachmentCount(Document document) {
        return document.getAttachments() == null ? 0 : document.getAttachments().size();
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private void showInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Anexos de SisTG");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, RuntimeException exception) {
        statusLabel.setText(title + ".");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error en anexos");
        alert.setHeaderText(title);
        alert.setContentText(exception.getMessage() == null
                ? exception.getClass().getSimpleName() : exception.getMessage());
        alert.showAndWait();
    }
}
