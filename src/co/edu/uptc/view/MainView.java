package co.edu.uptc.view;

import co.edu.uptc.controller.DocumentController;
import co.edu.uptc.controller.StudentController;
import co.edu.uptc.controller.TeacherController;
import co.edu.uptc.exception.BusinessException;
import co.edu.uptc.model.Alert;
import co.edu.uptc.model.Document;
import co.edu.uptc.model.DegreeWork;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JFileChooser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
        setSize(520, 440);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel(
                "Sistema de Seguimiento de Trabajos de Grado",
                SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(7, 1, 10, 10));
        JButton studentsButton = new JButton("Gestionar estudiantes");
        JButton teachersButton = new JButton("Gestionar docentes");
        JButton documentsButton = new JButton("Radicar y consultar propuestas");
        JButton boardButton = new JButton("Ver tablero de vencimientos");
        JButton alertsButton = new JButton("Consultar alertas");
        JButton exportAlertsButton = new JButton("Exportar alertas a TXT");
        JButton exitButton = new JButton("Salir");

        buttons.add(studentsButton);
        buttons.add(teachersButton);
        buttons.add(documentsButton);
        buttons.add(boardButton);
        buttons.add(alertsButton);
        buttons.add(exportAlertsButton);
        buttons.add(exitButton);
        add(buttons, BorderLayout.CENTER);

        studentsButton.addActionListener(e -> openStudents());
        teachersButton.addActionListener(e -> openTeachers());
        documentsButton.addActionListener(e -> openDocuments());
        boardButton.addActionListener(e -> showDeadlineBoard());
        alertsButton.addActionListener(e -> showAlerts(true));
        exportAlertsButton.addActionListener(e -> exportAlertsToTxt());
        exitButton.addActionListener(e -> dispose());

        showAlerts(false);
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

    private void showDeadlineBoard() {
        try {
            List<Document> board = documentController.obtenerTablero();
            if (board.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No hay trabajos de grado aprobados para mostrar.");
                return;
            }

            LocalDate today = LocalDate.now();
            StringBuilder message = new StringBuilder();
            message.append("Fecha de consulta: ").append(today).append("\n");
            message.append("Los días negativos indican que el trabajo ya venció.\n\n");

            for (Document document : board) {
                DegreeWork work = document.getDegreeWork();
                message.append("Expediente: ").append(document.getId()).append("\n");
                message.append("Título: ").append(document.getTitle()).append("\n");
                message.append("Aprobación: ").append(work.getApprovalDate()).append("\n");
                message.append("Presentación desde: ")
                        .append(work.getEarliestSubmissionDate()).append("\n");
                message.append("Fecha máxima: ").append(work.getDeadline()).append("\n");
                message.append("Días hasta el vencimiento: ")
                        .append(work.getRemainingDays(today)).append("\n");
                message.append("Estado del plazo: ")
                        .append(work.getDeadlineStatus(today).getDisplayName())
                        .append("\n\n");
            }

            JTextArea report = new JTextArea(message.toString(), 18, 48);
            report.setEditable(false);
            report.setLineWrap(true);
            report.setWrapStyleWord(true);
            report.setCaretPosition(0);

            JOptionPane.showMessageDialog(this, new JScrollPane(report),
                    "Tablero de vencimientos", JOptionPane.INFORMATION_MESSAGE);
        } catch (BusinessException | IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "No se pudo consultar el tablero", JOptionPane.ERROR_MESSAGE);
        }
    }

   private void showAlerts(boolean showWhenEmpty) {
        try {
            List<Alert> alerts = documentController.obtenerAlertas();

            if (alerts.isEmpty()) {
                if (showWhenEmpty) {
                    JOptionPane.showMessageDialog(this, "No hay alertas pendientes.");
                }
                return;
            }

            StringBuilder message = new StringBuilder();
            for (Alert alert : alerts) {
                message.append("[").append(alert.getType()).append("] ")
                        .append("Expediente: ").append(alert.getDocumentId())
                        .append(" (").append(alert.getReferenceDate()).append(")\n")
                        .append(alert.getMessage()).append("\n\n");
            }

            JTextArea report = new JTextArea(message.toString(), 15, 45);
            report.setEditable(false);
            report.setLineWrap(true);
            report.setWrapStyleWord(true);
            report.setCaretPosition(0);

            JOptionPane.showMessageDialog(this, new JScrollPane(report),
                    "Alertas del sistema", JOptionPane.WARNING_MESSAGE);
        } catch (BusinessException | IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "No se pudieron consultar las alertas", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportAlertsToTxt() {
        try {
            List<Alert> alerts = documentController.obtenerAlertas();
            if (alerts.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay alertas pendientes para exportar.");
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("alertas_" + LocalDate.now() + ".txt"));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("REPORTE DE ALERTAS - SISTG UPTC");
                writer.newLine();
                writer.write("Fecha de generación: " + LocalDate.now());
                writer.newLine();
                writer.write("Total de alertas: " + alerts.size());
                writer.newLine();
                writer.write("================================================");
                writer.newLine();
                writer.newLine();

                for (Alert alert : alerts) {
                    writer.write("Tipo: " + alert.getType());
                    writer.newLine();
                    writer.write("Expediente: " + alert.getDocumentId());
                    writer.newLine();
                    writer.write("Fecha de referencia: " + alert.getReferenceDate());
                    writer.newLine();
                    writer.write("Mensaje: " + alert.getMessage());
                    writer.newLine();
                    writer.write("------------------------------------------------");
                    writer.newLine();
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Alertas exportadas correctamente en:\n" + file.getAbsolutePath(),
                    "Exportar alertas", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar el archivo: " + e.getMessage(),
                    "Error al exportar", JOptionPane.ERROR_MESSAGE);
        } catch (BusinessException | IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "No se pudieron exportar las alertas", JOptionPane.ERROR_MESSAGE);
        }
    }
}
