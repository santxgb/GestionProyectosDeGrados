package co.edu.uptc.controller.fx;

import co.edu.uptc.main.Main;
import co.edu.uptc.controller.DocumentController;
import co.edu.uptc.controller.StudentController;
import co.edu.uptc.controller.TeacherController;
import co.edu.uptc.service.DocumentService;
import co.edu.uptc.service.StudentService;
import co.edu.uptc.service.TeacherService;
import co.edu.uptc.view.MainView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainFxController {

    @FXML
    private Label statusLabel;

    private MainView swingWindow;
    private Stage studentWindow;
    private Stage teacherWindow;
    private Stage documentWindow;
    private Stage deadlineBoardWindow;
    private Stage alertsWindow;
    private Stage juryWindow;

    @FXML
    private void onOpenSwing() {
        statusLabel.setText("Abriendo la interfaz existente...");
        SwingUtilities.invokeLater(() -> {
            try {
                if (swingWindow == null || !swingWindow.isDisplayable()) {
                    swingWindow = Main.createMainView();
                    swingWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                }
                swingWindow.setVisible(true);
                swingWindow.toFront();
                Platform.runLater(() -> statusLabel.setText("Interfaz Swing abierta correctamente."));
            } catch (RuntimeException exception) {
                Platform.runLater(() -> statusLabel.setText(
                        "No se pudo abrir Swing: " + exception.getMessage()));
            }
        });
    }


    @FXML
    private void onOpenStudents() {
        if (studentWindow != null && studentWindow.isShowing()) {
            studentWindow.toFront();
            studentWindow.requestFocus();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/co/edu/uptc/view/fxml/studentView.fxml"));
            Parent root = loader.load();
            StudentFxController fxController = loader.getController();
            fxController.setStudentController(new StudentController(new StudentService()));

            Stage stage = new Stage();
            stage.setTitle("SisTG UPTC - Gestión de estudiantes");
            stage.setScene(new Scene(root));
            stage.setMinWidth(850);
            stage.setMinHeight(630);
            stage.setOnHidden(event -> studentWindow = null);
            studentWindow = stage;
            stage.show();
            statusLabel.setText("Módulo de estudiantes JavaFX abierto.");
        } catch (IOException | RuntimeException exception) {
            statusLabel.setText("No se pudo abrir el módulo de estudiantes.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al abrir estudiantes");
            alert.setHeaderText("No se pudo cargar la ventana JavaFX");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onOpenTeachers() {
        if (teacherWindow != null && teacherWindow.isShowing()) {
            teacherWindow.toFront();
            teacherWindow.requestFocus();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/co/edu/uptc/view/fxml/teacherView.fxml"));
            Parent root = loader.load();
            TeacherFxController fxController = loader.getController();
            fxController.setTeacherController(new TeacherController(new TeacherService()));

            Stage stage = new Stage();
            stage.setTitle("SisTG UPTC - Gestión de docentes");
            stage.setScene(new Scene(root));
            stage.setMinWidth(850);
            stage.setMinHeight(630);
            stage.setOnHidden(event -> teacherWindow = null);
            teacherWindow = stage;
            stage.show();
            statusLabel.setText("Módulo de docentes JavaFX abierto.");
        } catch (IOException | RuntimeException exception) {
            statusLabel.setText("No se pudo abrir el módulo de docentes.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al abrir docentes");
            alert.setHeaderText("No se pudo cargar la ventana JavaFX");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onOpenDocuments() {
        if (documentWindow != null && documentWindow.isShowing()) {
            documentWindow.toFront();
            documentWindow.requestFocus();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/co/edu/uptc/view/fxml/documentView.fxml"));
            Parent root = loader.load();
            DocumentFxController fxController = loader.getController();
            fxController.setDocumentController(new DocumentController(new DocumentService()));

            Stage stage = new Stage();
            stage.setTitle("SisTG UPTC - Radicación y consulta de propuestas");
            stage.setScene(new Scene(root));
            stage.setMinWidth(1050);
            stage.setMinHeight(700);
            stage.setOnHidden(event -> documentWindow = null);
            documentWindow = stage;
            stage.show();
            statusLabel.setText("Módulo de propuestas JavaFX abierto.");
        } catch (IOException | RuntimeException exception) {
            statusLabel.setText("No se pudo abrir el módulo de propuestas.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al abrir propuestas");
            alert.setHeaderText("No se pudo cargar la ventana JavaFX");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onOpenDeadlineBoard() {
        if (deadlineBoardWindow != null && deadlineBoardWindow.isShowing()) {
            deadlineBoardWindow.toFront();
            deadlineBoardWindow.requestFocus();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/co/edu/uptc/view/fxml/deadlineBoardView.fxml"));
            Parent root = loader.load();
            DeadlineBoardFxController fxController = loader.getController();
            fxController.setDocumentController(new DocumentController(new DocumentService()));

            Stage stage = new Stage();
            stage.setTitle("SisTG UPTC - Tablero de trabajos de grado");
            stage.setScene(new Scene(root));
            stage.setMinWidth(1140);
            stage.setMinHeight(680);
            stage.setOnHidden(event -> deadlineBoardWindow = null);
            deadlineBoardWindow = stage;
            stage.show();
            statusLabel.setText("Tablero de vencimientos JavaFX abierto.");
        } catch (IOException | RuntimeException exception) {
            statusLabel.setText("No se pudo abrir el tablero de trabajos.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al abrir tablero");
            alert.setHeaderText("No se pudo cargar la ventana JavaFX");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
        }
    }


    @FXML
    private void onOpenAlerts() {
        if (alertsWindow != null && alertsWindow.isShowing()) {
            alertsWindow.toFront();
            alertsWindow.requestFocus();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/co/edu/uptc/view/fxml/alertsView.fxml"));
            Parent root = loader.load();
            AlertsFxController fxController = loader.getController();
            fxController.setDocumentController(new DocumentController(new DocumentService()));

            Stage stage = new Stage();
            stage.setTitle("SisTG UPTC - Consulta de alertas");
            stage.setScene(new Scene(root));
            stage.setMinWidth(890);
            stage.setMinHeight(610);
            stage.setOnHidden(event -> alertsWindow = null);
            alertsWindow = stage;
            stage.show();
            statusLabel.setText("Consulta de alertas JavaFX abierta.");
        } catch (IOException | RuntimeException exception) {
            statusLabel.setText("No se pudo abrir la consulta de alertas.");
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error al abrir alertas");
            dialog.setHeaderText("No se pudo cargar la ventana JavaFX");
            dialog.setContentText(exception.getMessage());
            dialog.showAndWait();
        }
    }

    @FXML
    private void onOpenJuries() {
        if (juryWindow != null && juryWindow.isShowing()) {
            juryWindow.toFront();
            juryWindow.requestFocus();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/co/edu/uptc/view/fxml/juryView.fxml"));
            Parent root = loader.load();
            JuryFxController fxController = loader.getController();
            fxController.setDocumentController(new DocumentController(new DocumentService()));

            Stage stage = new Stage();
            stage.setTitle("SisTG UPTC - Asignación de jurados");
            stage.setScene(new Scene(root));
            stage.setMinWidth(1020);
            stage.setMinHeight(710);
            stage.setOnHidden(event -> juryWindow = null);
            juryWindow = stage;
            stage.show();
            statusLabel.setText("Módulo de jurados JavaFX abierto.");
        } catch (IOException | RuntimeException exception) {
            statusLabel.setText("No se pudo abrir el módulo de jurados.");
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error al abrir jurados");
            dialog.setHeaderText("No se pudo cargar la ventana JavaFX");
            dialog.setContentText(exception.getMessage());
            dialog.showAndWait();
        }
    }

    /** Se llama una vez, después de mostrar la ventana principal JavaFX. */
    public void showStartupAlerts() {
        try {
            List<co.edu.uptc.model.Alert> alerts =
                    new DocumentController(new DocumentService()).obtenerAlertas();
            if (alerts.isEmpty()) {
                return;
            }
            statusLabel.setText("Hay " + alerts.size() + " alerta(s) pendiente(s).");
            ButtonType viewButton = new ButtonType("Ver alertas", ButtonBar.ButtonData.YES);
            ButtonType laterButton = new ButtonType("Ahora no", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert dialog = new Alert(Alert.AlertType.WARNING);
            dialog.setTitle("Alertas de SisTG");
            dialog.setHeaderText("Hay " + alerts.size() + " alerta(s) pendiente(s)");
            dialog.setContentText("¿Deseas consultar las alertas de propuestas y trabajos de grado?");
            dialog.getButtonTypes().setAll(viewButton, laterButton);
            if (dialog.showAndWait().orElse(laterButton) == viewButton) {
                onOpenAlerts();
            }
        } catch (RuntimeException exception) {
            statusLabel.setText("No se pudieron consultar las alertas al iniciar: "
                    + exception.getMessage());
        }
    }

    @FXML
    private void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acerca de SisTG");
        alert.setHeaderText("SisTG UPTC");
        alert.setContentText("Sistema de seguimiento de propuestas y trabajos de grado.\n"
                + "Programación II - UPTC, Seccional Sogamoso.\n\n"
                + "La interfaz se está migrando progresivamente de Swing a JavaFX.");
        alert.showAndWait();
    }

    @FXML
    private void onCloseJavaFx() {
        Platform.exit();
    }
}
