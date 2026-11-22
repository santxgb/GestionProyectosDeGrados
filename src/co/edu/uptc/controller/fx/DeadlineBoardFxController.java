package co.edu.uptc.controller.fx;

import co.edu.uptc.controller.DocumentController;
import co.edu.uptc.model.DegreeWork;
import co.edu.uptc.model.Document;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class DeadlineBoardFxController {

    @FXML private TableView<Document> boardTable;
    @FXML private TableColumn<Document, String> idColumn;
    @FXML private TableColumn<Document, String> titleColumn;
    @FXML private TableColumn<Document, String> modalityColumn;
    @FXML private TableColumn<Document, String> approvalDateColumn;
    @FXML private TableColumn<Document, String> earliestDateColumn;
    @FXML private TableColumn<Document, String> deadlineColumn;
    @FXML private TableColumn<Document, String> remainingDaysColumn;
    @FXML private TableColumn<Document, String> deadlineStatusColumn;
    @FXML private Label summaryLabel;
    @FXML private Label statusLabel;
    @FXML private TextArea detailsArea;
    @FXML private Button closeButton;

    private DocumentController documentController;
    private LocalDate referenceDate;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(safe(cell.getValue().getId())));
        titleColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(safe(cell.getValue().getTitle())));
        modalityColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getModality() == null
                        ? "-" : cell.getValue().getModality().getDisplayName()));
        approvalDateColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(date(cell.getValue().getDegreeWork().getApprovalDate())));
        earliestDateColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(date(cell.getValue().getDegreeWork().getEarliestSubmissionDate())));
        deadlineColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(date(cell.getValue().getDegreeWork().getDeadline())));
        remainingDaysColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(String.valueOf(
                        cell.getValue().getDegreeWork().getRemainingDays(referenceDate))));
        deadlineStatusColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getDegreeWork().getDeadlineStatus(referenceDate).getDisplayName()));

        for (TableColumn<Document, ?> column : boardTable.getColumns()) {
            column.setSortable(false);
        }
        boardTable.getSelectionModel().selectedItemProperty().addListener(
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
        String selectedId = null;
        Document selected = boardTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedId = selected.getId();
        }

        try {
            referenceDate = LocalDate.now();
            List<Document> works = documentController.obtenerTablero();
            boardTable.getSelectionModel().clearSelection();
            boardTable.getItems().setAll(works);
            summaryLabel.setText("Consulta: " + referenceDate
                    + "   |   Trabajos de grado: " + works.size());
            statusLabel.setText("Tablero actualizado. Ordenado de menor a mayor plazo restante.");
            detailsArea.clear();
            if (selectedId != null) {
                for (Document work : works) {
                    if (selectedId.equals(work.getId())) {
                        boardTable.getSelectionModel().select(work);
                        boardTable.scrollTo(work);
                        break;
                    }
                }
            }
        } catch (RuntimeException exception) {
            statusLabel.setText("No se pudo actualizar el tablero.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de consulta");
            alert.setHeaderText("No se pudo consultar el tablero de trabajos");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
        }
    }

    private void showDetails(Document document) {
        if (document == null || document.getDegreeWork() == null) {
            detailsArea.clear();
            return;
        }
        DegreeWork work = document.getDegreeWork();
        String students = document.getStudentIds() == null
                ? "-" : String.join(", ", document.getStudentIds());
        String notes = work.getObservations() == null || work.getObservations().isBlank()
                ? "Sin observaciones de seguimiento" : work.getObservations();

        StringBuilder details = new StringBuilder();
        details.append("Expediente: ").append(safe(document.getId())).append("\n")
                .append("Título: ").append(safe(document.getTitle())).append("\n")
                .append("Estudiantes (códigos): ").append(students).append("\n")
                .append("Director (ID): ").append(safe(document.getDirectorId())).append("\n")
                .append("Decisión del Comité: ")
                .append(document.getStatus() == null ? "-" : document.getStatus().getDisplayName()).append("\n")
                .append("Acta de aprobación: ").append(safe(work.getApprovalAct())).append("\n")
                .append("Fecha de aprobación: ").append(date(work.getApprovalDate())).append("\n")
                .append("Presentación desde: ").append(date(work.getEarliestSubmissionDate())).append("\n")
                .append("Fecha máxima: ").append(date(work.getDeadline())).append("\n")
                .append("Días restantes: ").append(work.getRemainingDays(referenceDate)).append("\n")
                .append("Estado del plazo: ")
                .append(work.getDeadlineStatus(referenceDate).getDisplayName()).append("\n\n")
                .append("Observaciones de seguimiento: ").append(notes);
        detailsArea.setText(details.toString());
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String date(LocalDate value) {
        return value == null ? "-" : value.toString();
    }

    @FXML
    private void onClose() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }
}
