package co.edu.uptc.controller.fx;

import co.edu.uptc.controller.DocumentController;
import co.edu.uptc.model.Document;
import co.edu.uptc.model.Jury;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class JuryFxController {

    @FXML private TableView<Document> workTable;
    @FXML private TableColumn<Document, String> idColumn;
    @FXML private TableColumn<Document, String> titleColumn;
    @FXML private TableColumn<Document, String> statusColumn;
    @FXML private TableColumn<Document, String> juriesColumn;
    @FXML private Label selectedWorkLabel;
    @FXML private Label currentJuriesLabel;
    @FXML private Label statusLabel;
    @FXML private TextField assignmentActField;

    @FXML private TextField jury1IdField;
    @FXML private TextField jury1LastNamesField;
    @FXML private TextField jury1FirstNamesField;
    @FXML private TextField jury1EmailField;
    @FXML private TextField jury1PhoneField;
    @FXML private CheckBox jury1ExternalCheck;
    @FXML private TextField jury1InstitutionField;

    @FXML private CheckBox secondJuryCheck;
    @FXML private GridPane jury2Pane;
    @FXML private TextField jury2IdField;
    @FXML private TextField jury2LastNamesField;
    @FXML private TextField jury2FirstNamesField;
    @FXML private TextField jury2EmailField;
    @FXML private TextField jury2PhoneField;
    @FXML private CheckBox jury2ExternalCheck;
    @FXML private TextField jury2InstitutionField;
    @FXML private Button closeButton;

    private DocumentController documentController;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(safe(cell.getValue().getId())));
        titleColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(safe(cell.getValue().getTitle())));
        statusColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getStatus() == null
                        ? "-" : cell.getValue().getStatus().getDisplayName()));
        juriesColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(String.valueOf(
                        cell.getValue().getDegreeWork().getJuries().size())));

        for (TableColumn<Document, ?> column : workTable.getColumns()) {
            column.setSortable(false); // Preserva el orden del tablero de vencimientos.
        }
        workTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, previous, selected) -> showWork(selected));
        secondJuryCheck.selectedProperty().addListener(
                (observable, previous, selected) -> jury2Pane.setDisable(!selected));
        jury1ExternalCheck.selectedProperty().addListener(
                (observable, previous, selected) -> jury1InstitutionField.setDisable(!selected));
        jury2ExternalCheck.selectedProperty().addListener(
                (observable, previous, selected) -> jury2InstitutionField.setDisable(!selected));

        clearForm();
    }

    public void setDocumentController(DocumentController controller) {
        this.documentController = controller;
        onRefresh();
    }

    @FXML
    private void onRefresh() {
        if (documentController == null) {
            return;
        }
        Document selected = workTable.getSelectionModel().getSelectedItem();
        String previousId = selected == null ? null : selected.getId();
        try {
            List<Document> works = documentController.obtenerTablero();
            workTable.getSelectionModel().clearSelection();
            workTable.getItems().setAll(works);
            clearForm();
            if (previousId != null) {
                for (Document work : works) {
                    if (previousId.equals(work.getId())) {
                        workTable.getSelectionModel().select(work);
                        workTable.scrollTo(work);
                        break;
                    }
                }
            }
            statusLabel.setText("Trabajos aprobados: " + works.size()
                    + ". Selecciona un expediente para consultar o asignar jurados.");
        } catch (RuntimeException exception) {
            showError("No se pudo consultar el tablero de trabajos", exception);
        }
    }

    private void showWork(Document document) {
        clearForm();
        if (document == null || document.getDegreeWork() == null) {
            return;
        }

        selectedWorkLabel.setText("Expediente: " + safe(document.getId())
                + "  |  " + safe(document.getTitle()));
        List<Jury> juries = document.getDegreeWork().getJuries();
        String act = document.getDegreeWork().getJuryAssignmentAct();
        assignmentActField.setText(act == null ? "" : act);
        if (juries.isEmpty()) {
            currentJuriesLabel.setText("Jurados actuales: sin asignar.");
            return;
        }
        StringBuilder details = new StringBuilder("Jurados actuales ("
                + juries.size() + "): ");
        for (int i = 0; i < juries.size(); i++) {
            Jury jury = juries.get(i);
            if (i > 0) {
                details.append("  |  ");
            }
            details.append(i + 1).append(". ")
                    .append(safe(jury.getId())).append(" - ")
                    .append(safe(jury.getLastNames())).append(", ")
                    .append(safe(jury.getFirstNames()));
        }
        currentJuriesLabel.setText(details.toString());
        loadFirstJury(juries.get(0));
        if (juries.size() > 1) {
            secondJuryCheck.setSelected(true);
            loadSecondJury(juries.get(1));
        }
    }

    @FXML
    private void onRestore() {
        onRefresh(); // Vuelve a leer los jurados guardados, sin escribir datos.
    }

    @FXML
    private void onSave() {
        Document selected = workTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getDegreeWork() == null) {
            showInformation("Selecciona un trabajo de grado aprobado en la tabla.");
            return;
        }
        if (documentController == null) {
            return;
        }

        List<Jury> juries = new ArrayList<>();
        juries.add(new Jury(text(jury1IdField), text(jury1LastNamesField),
                text(jury1FirstNamesField), text(jury1EmailField),
                text(jury1PhoneField), jury1ExternalCheck.isSelected(),
                jury1ExternalCheck.isSelected() ? text(jury1InstitutionField) : null));
        if (secondJuryCheck.isSelected()) {
            juries.add(new Jury(text(jury2IdField), text(jury2LastNamesField),
                    text(jury2FirstNamesField), text(jury2EmailField),
                    text(jury2PhoneField), jury2ExternalCheck.isSelected(),
                    jury2ExternalCheck.isSelected() ? text(jury2InstitutionField) : null));
        }

        boolean replacing = !selected.getDegreeWork().getJuries().isEmpty();
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar asignación de jurados");
        confirmation.setHeaderText(replacing ? "Reemplazar jurados del expediente "
                + selected.getId() : "Asignar jurados al expediente " + selected.getId());
        confirmation.setContentText("Se guardará la lista completa de " + juries.size()
                + " jurado(s) y el acta. ¿Deseas continuar?");
        confirmation.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        if (confirmation.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
            statusLabel.setText("Asignación cancelada. No se guardaron cambios.");
            return;
        }

        try {
            documentController.asignarJurados(selected.getId(),
                    text(assignmentActField), juries);
            onRefresh();
            statusLabel.setText("Jurados asignados correctamente al expediente "
                    + selected.getId() + ".");
            showInformation("Asignación de jurados guardada correctamente.");
        } catch (RuntimeException exception) {
            showError("No se pudo guardar la asignación de jurados", exception);
        }
    }

    private void loadFirstJury(Jury jury) {
        jury1IdField.setText(safeInput(jury.getId()));
        jury1LastNamesField.setText(safeInput(jury.getLastNames()));
        jury1FirstNamesField.setText(safeInput(jury.getFirstNames()));
        jury1EmailField.setText(safeInput(jury.getEmail()));
        jury1PhoneField.setText(safeInput(jury.getPhone()));
        jury1ExternalCheck.setSelected(jury.isExternal());
        jury1InstitutionField.setText(safeInput(jury.getInstitution()));
    }

    private void loadSecondJury(Jury jury) {
        jury2IdField.setText(safeInput(jury.getId()));
        jury2LastNamesField.setText(safeInput(jury.getLastNames()));
        jury2FirstNamesField.setText(safeInput(jury.getFirstNames()));
        jury2EmailField.setText(safeInput(jury.getEmail()));
        jury2PhoneField.setText(safeInput(jury.getPhone()));
        jury2ExternalCheck.setSelected(jury.isExternal());
        jury2InstitutionField.setText(safeInput(jury.getInstitution()));
    }

    private void clearForm() {
        selectedWorkLabel.setText("Ningún trabajo de grado seleccionado.");
        currentJuriesLabel.setText("Selecciona un expediente para consultar sus jurados.");
        assignmentActField.clear();
        jury1IdField.clear();
        jury1LastNamesField.clear();
        jury1FirstNamesField.clear();
        jury1EmailField.clear();
        jury1PhoneField.clear();
        jury1ExternalCheck.setSelected(false);
        jury1InstitutionField.clear();
        secondJuryCheck.setSelected(false);
        jury2IdField.clear();
        jury2LastNamesField.clear();
        jury2FirstNamesField.clear();
        jury2EmailField.clear();
        jury2PhoneField.clear();
        jury2ExternalCheck.setSelected(false);
        jury2InstitutionField.clear();
        jury2Pane.setDisable(true);
        jury1InstitutionField.setDisable(true);
        jury2InstitutionField.setDisable(true);
    }

    private String text(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private String safeInput(String value) {
        return value == null ? "" : value;
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private void showInformation(String message) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Gestión de jurados");
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        dialog.showAndWait();
    }

    private void showError(String heading, RuntimeException exception) {
        statusLabel.setText(heading + ".");
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle("Error de gestión de jurados");
        dialog.setHeaderText(heading);
        dialog.setContentText(exception.getMessage());
        dialog.showAndWait();
    }

    @FXML
    private void onClose() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }
}
