package co.edu.uptc.view;

import co.edu.uptc.controller.DocumentController;
import co.edu.uptc.exception.BusinessException;
import co.edu.uptc.model.Company;
import co.edu.uptc.model.Document;
import co.edu.uptc.model.enums.Modality;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class DocumentView extends JFrame {

    private final DocumentController documentController;
    private final Modality[] modalities;
    private final JTextField txtId;
    private final JTextField txtTitle;
    private final JComboBox<String> cmbModality;
    private final JTextField txtStudent1;
    private final JTextField txtStudent2;
    private final JTextField txtDirector;
    private final JTextField txtCodirector;
    private final JTextField txtFilingDate;
    private final JTextField txtCompany;
    private final JTextField txtObservations;

    public DocumentView(DocumentController documentController) {
        this.documentController = documentController;

        setTitle("Radicación de propuestas");
        setSize(680, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(content);

        content.add(new JLabel("Registre primero los estudiantes y docentes en sus respectivas ventanas."),
                BorderLayout.NORTH);

        txtId = new JTextField();
        txtTitle = new JTextField();
        txtStudent1 = new JTextField();
        txtStudent2 = new JTextField();
        txtDirector = new JTextField();
        txtCodirector = new JTextField();
        txtFilingDate = new JTextField();
        txtCompany = new JTextField();
        txtObservations = new JTextField();

        modalities = Modality.values();
        cmbModality = new JComboBox<>();
        for (Modality modality : modalities) {
            cmbModality.addItem(modality.getDisplayName());
        }

        JPanel form = new JPanel(new GridLayout(10, 2, 10, 10));
        form.add(new JLabel("Identificador del expediente:"));
        form.add(txtId);
        form.add(new JLabel("Título de la propuesta:"));
        form.add(txtTitle);
        form.add(new JLabel("Modalidad:"));
        form.add(cmbModality);
        form.add(new JLabel("Código del primer estudiante:"));
        form.add(txtStudent1);
        form.add(new JLabel("Código del segundo estudiante (opcional):"));
        form.add(txtStudent2);
        form.add(new JLabel("Identificación del director:"));
        form.add(txtDirector);
        form.add(new JLabel("Identificación del codirector (opcional):"));
        form.add(txtCodirector);
        form.add(new JLabel("Fecha de radicación (AAAA-MM-DD):"));
        form.add(txtFilingDate);
        form.add(new JLabel("Empresa/entidad (solo práctica, opcional):"));
        form.add(txtCompany);
        form.add(new JLabel("Observaciones iniciales (opcional):"));
        form.add(txtObservations);
        content.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton registerButton = new JButton("Radicar propuesta");
        JButton searchButton = new JButton("Consultar por ID");
        JButton clearButton = new JButton("Limpiar");
        buttons.add(registerButton);
        buttons.add(searchButton);
        buttons.add(clearButton);
        content.add(buttons, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> registerDocument());
        searchButton.addActionListener(e -> searchDocument());
        clearButton.addActionListener(e -> clearFields());
    }

    private void registerDocument() {
        try {
            List<String> studentCodes = new ArrayList<>();
            studentCodes.add(txtStudent1.getText().trim());
            if (!txtStudent2.getText().trim().isEmpty()) {
                studentCodes.add(txtStudent2.getText().trim());
            }

            LocalDate filingDate = LocalDate.parse(txtFilingDate.getText().trim());
            Modality modality = modalities[cmbModality.getSelectedIndex()];

            Document document = new Document(
                    txtId.getText().trim(),
                    txtTitle.getText().trim(),
                    modality,
                    studentCodes,
                    txtDirector.getText().trim(),
                    filingDate);

            String codirectorId = txtCodirector.getText().trim();
            if (!codirectorId.isEmpty()) {
                document.setCodirectorId(codirectorId);
            }

            String companyName = txtCompany.getText().trim();
            if (!companyName.isEmpty()) {
                document.setCompany(new Company(companyName, "", "", ""));
            }

            String observations = txtObservations.getText().trim();
            if (!observations.isEmpty()) {
                document.setObservations(observations);
            }

            documentController.registrarDocumento(document);
            JOptionPane.showMessageDialog(this, "Propuesta radicada correctamente.");
            clearFields();
        } catch (DateTimeParseException e) {
            showError("La fecha debe tener el formato AAAA-MM-DD y ser válida.");
        } catch (BusinessException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    private void searchDocument() {
        try {
            Document document = documentController.buscarDocumentoPorId(txtId.getText().trim());
            if (document == null) {
                JOptionPane.showMessageDialog(this, "No existe un expediente con ese identificador.");
                return;
            }

            StringBuilder message = new StringBuilder();
            message.append("Identificador: ").append(document.getId());
            message.append("\nTítulo: ").append(document.getTitle());
            message.append("\nModalidad: ").append(document.getModality().getDisplayName());
            message.append("\nEstudiantes: ").append(document.getStudentIds());
            message.append("\nDirector: ").append(document.getDirectorId());
            message.append("\nCodirector: ").append(document.getCodirectorId() == null
                    ? "No registrado" : document.getCodirectorId());
            message.append("\nRadicación: ").append(document.getFilingDate());
            message.append("\nEtapa: ").append(document.getStage().getDisplayName());
            message.append("\nEstado: ").append(document.getStatus().getDisplayName());
            if (document.getCompany() != null) {
                message.append("\nEmpresa/entidad: ").append(document.getCompany().getName());
            }
            if (document.getObservations() != null) {
                message.append("\nObservaciones: ").append(document.getObservations());
            }

            JOptionPane.showMessageDialog(this, message.toString(),
                    "Consulta de expediente", JOptionPane.INFORMATION_MESSAGE);
        } catch (BusinessException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtTitle.setText("");
        cmbModality.setSelectedIndex(0);
        txtStudent1.setText("");
        txtStudent2.setText("");
        txtDirector.setText("");
        txtCodirector.setText("");
        txtFilingDate.setText("");
        txtCompany.setText("");
        txtObservations.setText("");
        txtId.requestFocus();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
