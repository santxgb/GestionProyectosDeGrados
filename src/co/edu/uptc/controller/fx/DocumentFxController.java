package co.edu.uptc.controller.fx;

import co.edu.uptc.controller.DocumentController;
import co.edu.uptc.exception.BusinessException;
import co.edu.uptc.model.Attachment;
import co.edu.uptc.model.Company;
import co.edu.uptc.model.DegreeWork;
import co.edu.uptc.model.Document;
import co.edu.uptc.model.Jury;
import co.edu.uptc.model.enums.Modality;
import co.edu.uptc.model.enums.Stage;
import co.edu.uptc.model.enums.Status;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Recibe los eventos de documentView.fxml; la lógica se delega al DocumentController. */
public class DocumentFxController {

    @FXML private TextField idField;
    @FXML private TextField titleField;
    @FXML private ComboBox<Modality> modalityCombo;
    @FXML private DatePicker filingDatePicker;
    @FXML private TextField student1Field;
    @FXML private TextField student2Field;
    @FXML private TextField directorField;
    @FXML private TextField codirectorField;
    @FXML private TextField companyField;
    @FXML private TextArea observationsField;

    @FXML private TextField searchIdField;
    @FXML private TableView<Document> documentTable;
    @FXML private TableColumn<Document, String> idColumn;
    @FXML private TableColumn<Document, String> titleColumn;
    @FXML private TableColumn<Document, String> modalityColumn;
    @FXML private TableColumn<Document, String> stageColumn;
    @FXML private TableColumn<Document, String> statusColumn;
    @FXML private TableColumn<Document, String> filingDateColumn;
    @FXML private TextArea detailsArea;
    @FXML private Label statusLabel;
    @FXML private Button closeButton;

    private DocumentController documentController;

    @FXML
    private void initialize() {
        modalityCombo.getItems().setAll(Arrays.asList(Modality.values()));
        modalityCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Modality modality) {
                return modality == null ? "" : modality.getDisplayName();
            }

            @Override
            public Modality fromString(String text) {
                // ComboBox no editable: solo permite elegir un valor del enum.
                return null;
            }
        });
        modalityCombo.getSelectionModel().selectFirst();
        filingDatePicker.setValue(LocalDate.now());

        idColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(safe(cell.getValue().getId())));
        titleColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(safe(cell.getValue().getTitle())));
        modalityColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().getModality() == null ? "" : cell.getValue().getModality().getDisplayName()));
        stageColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().getStage() == null ? "" : cell.getValue().getStage().getDisplayName()));
        statusColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().getStatus() == null ? "" : cell.getValue().getStatus().getDisplayName()));
        filingDateColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().getFilingDate() == null ? "" : cell.getValue().getFilingDate().toString()));

        documentTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, previous, selected) -> {
                    if (selected != null) {
                        showDetails(selected);
                    }
                });
    }

    /** Se llama desde MainFxController, una vez FXMLLoader ha creado la vista. */
    public void setDocumentController(DocumentController documentController) {
        this.documentController = documentController;
        onRefresh();
    }

    @FXML
    private void onRegister() {
        try {
            Document document = readNewProposal();
            documentController.registrarDocumento(document);
            refreshTable();
            clearForm();
            showInformation("Propuesta radicada correctamente: " + document.getId());
        } catch (BusinessException | IllegalStateException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void onSearch() {
        try {
            Document document = documentController.buscarDocumentoPorId(searchIdField.getText().trim());
            if (document == null) {
                documentTable.getSelectionModel().clearSelection();
                detailsArea.setText("No existe un expediente con ese identificador.");
                statusLabel.setText("No se encontró el expediente solicitado.");
                return;
            }
            refreshTable();
            boolean foundInTable = false;
            for (Document item : documentTable.getItems()) {
                if (document.getId().equals(item.getId())) {
                    documentTable.getSelectionModel().select(item);
                    documentTable.scrollTo(item);
                    foundInTable = true;
                    break;
                }
            }
            if (!foundInTable) {
                showDetails(document);
            }
            statusLabel.setText("Expediente encontrado: " + document.getId());
        } catch (BusinessException | IllegalStateException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void onRefresh() {
        try {
            refreshTable();
            documentTable.getSelectionModel().clearSelection();
            searchIdField.clear();
            detailsArea.setText("Selecciona un expediente para consultar sus datos.");
        } catch (BusinessException | IllegalStateException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void onClear() {
        clearForm();
        statusLabel.setText("Formulario de radicación limpio.");
    }

    @FXML
    private void onClose() {
        ((javafx.stage.Stage) closeButton.getScene().getWindow()).close();
    }

    private Document readNewProposal() {
        List<String> studentCodes = new ArrayList<>();
        studentCodes.add(student1Field.getText().trim());
        if (!student2Field.getText().trim().isEmpty()) {
            studentCodes.add(student2Field.getText().trim());
        }

        Document document = new Document(
                idField.getText().trim(),
                titleField.getText().trim(),
                modalityCombo.getValue(),
                studentCodes,
                directorField.getText().trim(),
                filingDatePicker.getValue());

        String codirectorId = codirectorField.getText().trim();
        if (!codirectorId.isEmpty()) {
            document.setCodirectorId(codirectorId);
        }
        String companyName = companyField.getText().trim();
        if (!companyName.isEmpty()) {
            document.setCompany(new Company(companyName, "", "", ""));
        }
        String observations = observationsField.getText().trim();
        if (!observations.isEmpty()) {
            document.setObservations(observations);
        }
        return document;
    }

    private void refreshTable() {
        documentTable.getItems().setAll(documentController.listarDocumentos());
        statusLabel.setText("Expedientes registrados: " + documentTable.getItems().size());
    }

    private void clearForm() {
        idField.clear();
        titleField.clear();
        modalityCombo.getSelectionModel().selectFirst();
        filingDatePicker.setValue(LocalDate.now());
        student1Field.clear();
        student2Field.clear();
        directorField.clear();
        codirectorField.clear();
        companyField.clear();
        observationsField.clear();
        idField.requestFocus();
    }

    private void showDetails(Document document) {
        StringBuilder text = new StringBuilder();
        text.append("Identificador: ").append(safe(document.getId()));
        text.append("\nTítulo: ").append(safe(document.getTitle()));
        text.append("\nModalidad: ").append(document.getModality() == null ? "" : document.getModality().getDisplayName());
        text.append("\nEtapa: ").append(document.getStage() == null ? "" : document.getStage().getDisplayName());
        text.append("\nEstado: ").append(document.getStatus() == null ? "" : document.getStatus().getDisplayName());
        text.append("\nEstudiantes (códigos): ").append(document.getStudentIds());
        text.append("\nDirector (ID): ").append(safe(document.getDirectorId()));
        text.append("\nCodirector (ID): ").append(emptyAsNotRegistered(document.getCodirectorId()));
        text.append("\nRadicación: ").append(document.getFilingDate());

        if (document.getStage() == Stage.PROPUESTA && document.getStatus() == Status.EN_REVISION
                && document.getFilingDate() != null) {
            text.append("\nPlazo de decisión del Comité: ").append(document.getReviewDueDate());
        }
        if (document.getDecisionDate() != null) {
            text.append("\nDecisión del Comité: ").append(document.getDecisionDate());
            text.append("\nActa de decisión: ").append(emptyAsNotRegistered(document.getStatusAct()));
        }
        Company company = document.getCompany();
        if (company != null) {
            text.append("\nEmpresa/entidad: ").append(safe(company.getName()));
        }
        text.append("\nObservaciones iniciales de la propuesta: ")
                .append(emptyAsNotRegistered(document.getObservations()));

        DegreeWork work = document.getDegreeWork();
        if (work != null) {
            text.append("\n\nTRABAJO DE GRADO");
            text.append("\nAprobación: ").append(work.getApprovalDate());
            if (work.getApprovalDate() != null) {
                text.append("\nPresentación desde: ").append(work.getEarliestSubmissionDate());
                text.append("\nFecha máxima de presentación: ").append(work.getDeadline());
            }
            text.append("\nObservaciones de seguimiento: ").append(emptyAsNotRegistered(work.getObservations()));
            text.append("\nActa de asignación de jurados: ").append(emptyAsNotRegistered(work.getJuryAssignmentAct()));
            List<Jury> juries = work.getJuries();
            if (juries.isEmpty()) {
                text.append("\nJurados: Sin asignar");
            } else {
                for (Jury jury : juries) {
                    text.append("\nJurado: ").append(jury.getFullName());
                }
            }
        }
        List<Attachment> attachments = document.getAttachments();
        if (attachments != null && !attachments.isEmpty()) {
            text.append("\n\nANEXOS");
            for (Attachment attachment : attachments) {
                text.append("\n").append(safe(attachment.getName()))
                        .append(" · ").append(safe(attachment.getPath()));
            }
        }
        detailsArea.setText(text.toString());
        detailsArea.positionCaret(0);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String emptyAsNotRegistered(String value) {
        return value == null || value.trim().isEmpty() ? "No registrado" : value;
    }

    private void showInformation(String message) {
        statusLabel.setText(message);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle("SisTG UPTC");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showError(String message) {
        statusLabel.setText("Error: " + message);
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error en la propuesta");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
