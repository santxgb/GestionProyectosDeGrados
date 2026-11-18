package co.edu.uptc.view;

import co.edu.uptc.controller.DocumentController;
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

    public MainView(DocumentController documentController) {
        this.documentController = documentController;

        setTitle("SisTG UPTC");
        setSize(520, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel(
                "Sistema de Seguimiento de Trabajos de Grado",
                SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(2, 1, 10, 10));
        JButton alertsButton = new JButton("Consultar alertas");
        JButton exitButton = new JButton("Salir");

        buttons.add(alertsButton);
        buttons.add(exitButton);
        add(buttons, BorderLayout.CENTER);

        alertsButton.addActionListener(e -> showAlerts());
        exitButton.addActionListener(e -> dispose());
    }

    private void showAlerts() {
        try {
            List<Alert> alerts = documentController.obtenerAlertas();

            if (alerts.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "No hay alertas pendientes.");
                return;
            }

            StringBuilder message = new StringBuilder();
            for (Alert alert : alerts) {
                message.append(alert.getMessage()).append("\n\n");
            }

            JOptionPane.showMessageDialog(this, message.toString(),
                    "Alertas del sistema",
                    JOptionPane.WARNING_MESSAGE);

        } catch (BusinessException | IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "No se pudieron consultar las alertas",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
