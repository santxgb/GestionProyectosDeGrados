package co.edu.uptc.controller.fx;

import co.edu.uptc.controller.TeacherController;
import co.edu.uptc.exception.BusinessException;
import co.edu.uptc.model.Teacher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.Optional;

/** Eventos de teacherView.fxml; todas las reglas se delegan a TeacherController. */
public class TeacherFxController {

    @FXML private TextField idField;
    @FXML private TextField firstNamesField;
    @FXML private TextField lastNamesField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;

    @FXML private TableView<Teacher> teacherTable;
    @FXML private TableColumn<Teacher, String> idColumn;
    @FXML private TableColumn<Teacher, String> lastNamesColumn;
    @FXML private TableColumn<Teacher, String> firstNamesColumn;
    @FXML private TableColumn<Teacher, String> emailColumn;
    @FXML private TableColumn<Teacher, String> phoneColumn;
    @FXML private Label statusLabel;
    @FXML private Button closeButton;

    private TeacherController teacherController;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        lastNamesColumn.setCellValueFactory(new PropertyValueFactory<>("lastNames"));
        firstNamesColumn.setCellValueFactory(new PropertyValueFactory<>("firstNames"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        teacherTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, previousTeacher, selectedTeacher) -> {
                    if (selectedTeacher != null) {
                        fillForm(selectedTeacher);
                    }
                });
    }

    /** Se configura desde MainFxController después de cargar el archivo FXML. */
    public void setTeacherController(TeacherController teacherController) {
        this.teacherController = teacherController;
        onRefresh();
    }

    @FXML
    private void onRegister() {
        try {
            teacherController.registrarDocente(readForm());
            refreshTable();
            clearForm();
            showInformation("Docente registrado correctamente.");
        } catch (BusinessException | IllegalStateException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void onSearch() {
        try {
            Teacher teacher = teacherController.buscarProfesorPorId(idField.getText().trim());
            if (teacher == null) {
                showInformation("No se encontró un docente con esa identificación.");
                return;
            }
            fillForm(teacher);
            teacherTable.getSelectionModel().clearSelection();
            for (Teacher item : teacherTable.getItems()) {
                if (teacher.getId().equals(item.getId())) {
                    teacherTable.getSelectionModel().select(item);
                    teacherTable.scrollTo(item);
                    break;
                }
            }
            statusLabel.setText("Docente encontrado: " + teacher.getId());
        } catch (BusinessException | IllegalStateException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void onUpdate() {
        Teacher teacher = readForm();
        if (!confirm("Confirmar actualización",
                "¿Confirma la actualización del docente con identificación " + teacher.getId() + "?")) {
            return;
        }
        try {
            teacherController.actualizarDocente(teacher);
            refreshTable();
            clearForm();
            showInformation("Docente actualizado correctamente.");
        } catch (BusinessException | IllegalStateException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        try {
            String id = idField.getText().trim();
            Teacher teacher = teacherController.buscarProfesorPorId(id);
            if (teacher == null) {
                showError("No se encontró un docente con esa identificación.");
                return;
            }
            if (!confirm("Confirmar eliminación",
                    "¿Confirma que desea eliminar al docente " + teacher.getFirstNames() + " "
                            + teacher.getLastNames() + " (" + id + ")?")) {
                return;
            }
            teacherController.eliminarDocente(id);
            refreshTable();
            clearForm();
            showInformation("Docente eliminado correctamente.");
        } catch (BusinessException | IllegalStateException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void onRefresh() {
        try {
            clearForm();
            refreshTable();
        } catch (BusinessException | IllegalStateException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void onClear() {
        clearForm();
        statusLabel.setText("Formulario limpio. Puedes registrar o buscar un docente.");
    }

    @FXML
    private void onClose() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }

    private Teacher readForm() {
        return new Teacher(
                idField.getText().trim(),
                lastNamesField.getText().trim(),
                firstNamesField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim());
    }

    private void fillForm(Teacher teacher) {
        idField.setText(safe(teacher.getId()));
        idField.setEditable(false);
        firstNamesField.setText(safe(teacher.getFirstNames()));
        lastNamesField.setText(safe(teacher.getLastNames()));
        emailField.setText(safe(teacher.getEmail()));
        phoneField.setText(safe(teacher.getPhone()));
    }

    private void clearForm() {
        teacherTable.getSelectionModel().clearSelection();
        idField.setEditable(true);
        idField.clear();
        firstNamesField.clear();
        lastNamesField.clear();
        emailField.clear();
        phoneField.clear();
        idField.requestFocus();
    }

    private void refreshTable() {
        teacherTable.getItems().setAll(teacherController.listarProfesores());
        statusLabel.setText("Docentes registrados: " + teacherTable.getItems().size());
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(null);
        Optional<ButtonType> answer = alert.showAndWait();
        return answer.isPresent() && answer.get() == ButtonType.YES;
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
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
