package co.edu.uptc.view;

import co.edu.uptc.controller.DocumentController;
import co.edu.uptc.controller.StudentController;
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

    /** Recibe los controladores utilizados por la ventana principal. */
    public MainView(DocumentController documentController, StudentController studentController) {
        this.documentController = documentController;
        this.studentController = studentController;

        setTitle("SisTG UPTC");
        setSize(520, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel(
                "Sistema de Seguimiento de Trabajos de Grado",
                SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton studentsButton = new JButton("Gestionar estudiantes");
        JButton alertsButton = new JButton("Consultar alertas");
        JButton exitButton = new JButton("Salir");

        buttons.add(studentsButton);
        buttons.add(alertsButton);
        buttons.add(exitButton);
        add(buttons, BorderLayout.CENTER);

        studentsButton.addActionListener(e -> openStudents());
        alertsButton.addActionListener(e -> showAlerts());
        exitButton.addActionListener(e -> dispose());
    }

    private void openStudents() {
        StudentView studentView = new StudentView(studentController);
        studentView.setVisible(true);
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
