package co.edu.uptc.view;

import co.edu.uptc.controller.DocumentController;
import co.edu.uptc.controller.StudentController;
import co.edu.uptc.controller.TeacherController;
import co.edu.uptc.exception.BusinessException;
import co.edu.uptc.model.Alert;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

public class MainView extends JFrame {

    private final DocumentController documentController;
    private final StudentController studentController;
    private final TeacherController teacherController;

    public MainView(DocumentController documentController, StudentController studentController,
                    TeacherController teacherController) {
        this.documentController = documentController;
        this.studentController = studentController;
        this.teacherController = teacherController;

        setTitle("SisTG UPTC");
        setSize(520, 390);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel(
                "Sistema de Seguimiento de Trabajos de Grado",
                SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(5, 1, 10, 10));
        JButton studentsButton = new JButton("Gestionar estudiantes");
        JButton teachersButton = new JButton("Gestionar docentes");
        JButton documentsButton = new JButton("Radicar y consultar propuestas");
        JButton alertsButton = new JButton("Consultar alertas");
        JButton exitButton = new JButton("Salir");

        buttons.add(studentsButton);
        buttons.add(teachersButton);
        buttons.add(documentsButton);
        buttons.add(alertsButton);
        buttons.add(exitButton);
        add(buttons, BorderLayout.CENTER);

        studentsButton.addActionListener(e -> openStudents());
        teachersButton.addActionListener(e -> openTeachers());
        documentsButton.addActionListener(e -> openDocuments());
        alertsButton.addActionListener(e -> showAlerts());
        exitButton.addActionListener(e -> dispose());
    }

    private void openStudents() {
        StudentView studentView = new StudentView(studentController);
        studentView.setVisible(true);
    }

    private void openTeachers() {
        TeacherView teacherView = new TeacherView(teacherController);
        teacherView.setVisible(true);
    }

    private void openDocuments() {
        DocumentView documentView = new DocumentView(documentController);
        documentView.setVisible(true);
    }

    private void showAlerts() {
        try {
            List<Alert> alerts = documentController.obtenerAlertas();

            if (alerts.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay alertas pendientes.");
                return;
            }

            StringBuilder message = new StringBuilder();
            for (Alert alert : alerts) {
                message.append(alert.getMessage()).append("\n\n");
            }

            JOptionPane.showMessageDialog(this, message.toString(),
                    "Alertas del sistema", JOptionPane.WARNING_MESSAGE);
        } catch (BusinessException | IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "No se pudieron consultar las alertas", JOptionPane.ERROR_MESSAGE);
        }
    }
}
