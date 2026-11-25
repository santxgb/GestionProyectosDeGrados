package co.edu.uptc.controller.fx;

import co.edu.uptc.controller.StudentController;
import co.edu.uptc.exception.BusinessException;
import co.edu.uptc.model.Student;
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

public class StudentFxController {

    @FXML private TextField codeField;
    @FXML private TextField idNumberField;
    @FXML private TextField firstNamesField;
    @FXML private TextField lastNamesField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> codeColumn;
    @FXML private TableColumn<Student, String> lastNamesColumn;
    @FXML private TableColumn<Student, String> firstNamesColumn;
    @FXML private TableColumn<Student, String> emailColumn;
    @FXML private TableColumn<Student, String> phoneColumn;
    @FXML private Label statusLabel;
    @FXML private Button closeButton;

    private StudentController studentController;

    @FXML
    private void initialize() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        lastNamesColumn.setCellValueFactory(new PropertyValueFactory<>("lastNames"));
        firstNamesColumn.setCellValueFactory(new PropertyValueFactory<>("firstNames"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        studentTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, previousStudent, selectedStudent) -> {
                    if (selectedStudent != null) {
                        fillForm(selectedStudent);
                    }
                });
    }

    public void setStudentController(StudentController studentController) {
        this.studentController = studentController;
        onRefresh();
    }

    @FXML
    private void onRegister() {
        try {
            studentController.registrarEstudiante(readForm());
            refreshTable();
            clearForm();
            showInformation("Estudiante registrado correctamente.");
        } catch (BusinessException | IllegalStateException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void onSearch() {
        try {
            Student student = studentController.buscarEstudiantePorCodigo(codeField.getText().trim());
            if (student == null) {
                showInformation("No se encontró un estudiante con ese código.");
                return;
            }
            fillForm(student);
            studentTable.getSelectionModel().clearSelection();
            for (Student item : studentTable.getItems()) {
                if (student.getCode().equals(item.getCode())) {
                    studentTable.getSelectionModel().select(item);
                    studentTable.scrollTo(item);
                    break;
                }
            }
            statusLabel.setText("Estudiante encontrado: " + student.getCode());
        } catch (BusinessException | IllegalStateException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void onUpdate() {
        Student student = readForm();
        if (!confirm("Confirmar actualización",
                "¿Confirma la actualización del estudiante con código " + student.getCode() + "?")) {
            return;
        }
        try {
            studentController.actualizarEstudiante(student);
            refreshTable();
            clearForm();
            showInformation("Estudiante actualizado correctamente.");
        } catch (BusinessException | IllegalStateException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        try {
            String code = codeField.getText().trim();
            Student student = studentController.buscarEstudiantePorCodigo(code);
            if (student == null) {
                showError("No se encontró un estudiante con ese código.");
                return;
            }
            if (!confirm("Confirmar eliminación",
                    "¿Confirma que desea eliminar al estudiante " + student.getFirstNames() + " "
                            + student.getLastNames() + " (" + code + ")?")) {
                return;
            }
            studentController.eliminarEstudiante(code);
            refreshTable();
            clearForm();
            showInformation("Estudiante eliminado correctamente.");
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
        statusLabel.setText("Formulario limpio. Puedes registrar o buscar un estudiante.");
    }

    @FXML
    private void onClose() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }

    private Student readForm() {
        return new Student(
                idNumberField.getText().trim(),
                codeField.getText().trim(),
                lastNamesField.getText().trim(),
                firstNamesField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim());
    }

    private void fillForm(Student student) {
        codeField.setText(safe(student.getCode()));
        codeField.setEditable(false);
        idNumberField.setText(safe(student.getIdNumber()));
        firstNamesField.setText(safe(student.getFirstNames()));
        lastNamesField.setText(safe(student.getLastNames()));
        emailField.setText(safe(student.getEmail()));
        phoneField.setText(safe(student.getPhone()));
    }

    private void clearForm() {
        studentTable.getSelectionModel().clearSelection();
        codeField.setEditable(true);
        codeField.clear();
        idNumberField.clear();
        firstNamesField.clear();
        lastNamesField.clear();
        emailField.clear();
        phoneField.clear();
        codeField.requestFocus();
    }

    private void refreshTable() {
        studentTable.getItems().setAll(studentController.listarEstudiantes());
        statusLabel.setText("Estudiantes registrados: " + studentTable.getItems().size());
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
