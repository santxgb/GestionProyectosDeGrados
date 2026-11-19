package co.edu.uptc.view;

import co.edu.uptc.controller.DocumentController;
import co.edu.uptc.exception.BusinessException;
import co.edu.uptc.model.Company;
import co.edu.uptc.model.Document;
import co.edu.uptc.model.Jury;
import co.edu.uptc.model.enums.Modality;
import co.edu.uptc.model.enums.Status;
import co.edu.uptc.model.Attachment;

import javax.swing.JFileChooser;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
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

        setTitle("Gestión de expedientes");
        setSize(760, 620);
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

        JPanel buttons = new JPanel(new GridLayout(3, 3, 8, 8));
        JButton registerButton = new JButton("Radicar propuesta");
        JButton searchButton = new JButton("Consultar por ID");
        JButton listAllButton = new JButton("Listar expedientes");
        JButton updateButton = new JButton("Actualizar propuesta");
        JButton deleteButton = new JButton("Eliminar propuesta");
        JButton decisionButton = new JButton("Registrar decisión");
        JButton juriesButton = new JButton("Asignar jurados");
        JButton attachmentButton = new JButton("Agregar anexo");
        JButton observationsButton = new JButton("Actualizar observaciones");
        JButton clearButton = new JButton("Limpiar");

        buttons.add(registerButton);
        buttons.add(searchButton);
        buttons.add(listAllButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(decisionButton);
        buttons.add(juriesButton);
        buttons.add(attachmentButton);
        buttons.add(observationsButton);
        buttons.add(clearButton);
        content.add(buttons, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> registerDocument());
        searchButton.addActionListener(e -> searchDocument());
        listAllButton.addActionListener(e -> listAllDocuments());
        updateButton.addActionListener(e -> updateDocument());
        deleteButton.addActionListener(e -> deleteDocument());
        decisionButton.addActionListener(e -> registerDecision());
        juriesButton.addActionListener(e -> assignJuries());
        attachmentButton.addActionListener(e -> addAttachment());
        observationsButton.addActionListener(e -> updateObservations());
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
            txtId.setText(document.getId());
            txtTitle.setText(document.getTitle());
            cmbModality.setSelectedItem(document.getModality().getDisplayName());
            txtStudent1.setText(document.getStudentIds().get(0));
            txtStudent2.setText(document.getStudentIds().size() > 1 ? document.getStudentIds().get(1) : "");
            txtDirector.setText(document.getDirectorId());
            txtCodirector.setText(document.getCodirectorId() == null ? "" : document.getCodirectorId());
            txtFilingDate.setText(document.getFilingDate().toString());
            txtCompany.setText(document.getCompany() == null ? "" : document.getCompany().getName());
            txtObservations.setText(document.getObservations() == null ? "" : document.getObservations());

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
            if (document.getDecisionDate() != null) {
                message.append("\nFecha de decisión: ").append(document.getDecisionDate());
                message.append("\nActa de decisión: ").append(document.getStatusAct());
            }
            if (document.getDegreeWork() != null) {
                message.append("\nPresentación desde: ")
                        .append(document.getDegreeWork().getEarliestSubmissionDate());
                message.append("\nVencimiento: ").append(document.getDegreeWork().getDeadline());
                List<Jury> juries = document.getDegreeWork().getJuries();
                if (juries.isEmpty()) {
                    message.append("\nJurados: Sin asignar");
                } else {
                    message.append("\nActa de asignación de jurados: ")
                            .append(document.getDegreeWork().getJuryAssignmentAct());
                    for (int i = 0; i < juries.size(); i++) {
                        Jury jury = juries.get(i);
                        message.append("\nJurado ").append(i + 1).append(": ")
                                .append(jury.getFullName());
                        message.append("\n  Identificación: ").append(jury.getId());
                        message.append("\n  Correo: ").append(jury.getEmail());
                        message.append("\n  Teléfono: ").append(jury.getPhone());
                        message.append("\n  Tipo: ")
                                .append(jury.isExternal() ? "Externo" : "Interno");
                        if (jury.isExternal()) {
                            message.append("\n  Institución: ").append(jury.getInstitution());
                        }
                    }
                }
                String followUp = document.getDegreeWork().getObservations();
                message.append("\nObservaciones de seguimiento: ")
                        .append(followUp == null || followUp.trim().isEmpty()
                                ? "Sin observaciones" : followUp);
            }
            if (document.getCompany() != null) {
                message.append("\nEmpresa/entidad: ").append(document.getCompany().getName());
            }
            String initialObservations = document.getObservations();
            message.append("\nObservaciones iniciales de la propuesta: ")
                    .append(initialObservations == null || initialObservations.trim().isEmpty()
                            ? "Sin observaciones" : initialObservations);
            List<Attachment> attachments = document.getAttachments();
            if (attachments.isEmpty()) {
                message.append("\nAnexos: Sin anexos registrados");
            } else {
                for (int i = 0; i < attachments.size(); i++) {
                    Attachment att = attachments.get(i);
                    message.append("\nAnexo ").append(i + 1).append(": ").append(att.getName())
                            .append(" (").append(att.getType()).append(" - ").append(att.getUploadDate()).append(")")
                            .append("\n  Ruta: ").append(att.getPath());
                }
            }

            JTextArea details = new JTextArea(message.toString(), 18, 48);
            details.setEditable(false);
            details.setLineWrap(true);
            details.setWrapStyleWord(true);
            details.setCaretPosition(0);
            JOptionPane.showMessageDialog(this, new JScrollPane(details),
                    "Consulta de expediente", JOptionPane.INFORMATION_MESSAGE);
        } catch (BusinessException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    private void registerDecision() {
        JTextField decisionId = new JTextField(txtId.getText().trim());
        JTextField decisionDate = new JTextField();
        JTextField decisionAct = new JTextField();

        Status[] decisions = {
                Status.APROBADO,
                Status.APROBADO_CON_MODIFICACIONES,
                Status.NO_APROBADO
        };
        JComboBox<String> decisionOptions = new JComboBox<>();
        for (Status decision : decisions) {
            decisionOptions.addItem(decision.getDisplayName());
        }

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.add(new JLabel("Identificador de la propuesta:"));
        form.add(decisionId);
        form.add(new JLabel("Decisión del Comité:"));
        form.add(decisionOptions);
        form.add(new JLabel("Fecha de decisión (AAAA-MM-DD):"));
        form.add(decisionDate);
        form.add(new JLabel("Número o referencia del acta:"));
        form.add(decisionAct);

        int option = JOptionPane.showConfirmDialog(this, form,
                "Registrar decisión del Comité Curricular",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String id = decisionId.getText().trim();
            Status decision = decisions[decisionOptions.getSelectedIndex()];
            LocalDate date = LocalDate.parse(decisionDate.getText().trim());
            String act = decisionAct.getText().trim();

            int confirmation = JOptionPane.showConfirmDialog(this,
                    "¿Confirma la decisión «" + decision.getDisplayName()
                            + "» para el expediente " + id + "?\n"
                            + "Una vez registrada, no podrá volver a decidir esta propuesta.",
                    "Confirmar decisión", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }

            documentController.registrarDecisionDocumento(id, decision, date, act);
            Document updated = documentController.buscarDocumentoPorId(id);
            txtId.setText(id);

            StringBuilder message = new StringBuilder("Decisión registrada correctamente.");
            message.append("\nEstado: ").append(updated.getStatus().getDisplayName());
            if (updated.getDegreeWork() != null) {
                message.append("\nSe generó el trabajo de grado.");
                message.append("\nPresentación desde: ")
                        .append(updated.getDegreeWork().getEarliestSubmissionDate());
                message.append("\nFecha máxima: ")
                        .append(updated.getDegreeWork().getDeadline());
            } else {
                message.append("\nLa propuesta no genera trabajo de grado.");
            }
            JOptionPane.showMessageDialog(this, message.toString(),
                    "Decisión del Comité", JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException e) {
            showError("La fecha debe tener el formato AAAA-MM-DD y ser válida.");
        } catch (BusinessException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    private void assignJuries() {
        String id = txtId.getText().trim();

        try {
            Document document = documentController.buscarDocumentoPorId(id);
            if (document == null) {
                showError("No existe un expediente con ese identificador.");
                return;
            }
            if (document.getDegreeWork() == null) {
                showError("Solo se pueden asignar jurados a un trabajo de grado aprobado.");
                return;
            }

            if (!document.getDegreeWork().getJuries().isEmpty()) {
                int replace = JOptionPane.showConfirmDialog(this,
                        "Este trabajo ya tiene jurados asignados.\n"
                                + "¿Desea reemplazarlos?",
                        "Reemplazar jurados", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (replace != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            JComboBox<String> count = new JComboBox<>(new String[]{"1", "2"});
            JTextField act = new JTextField();
            JPanel initialForm = new JPanel(new GridLayout(2, 2, 10, 10));
            initialForm.add(new JLabel("Cantidad de jurados:"));
            initialForm.add(count);
            initialForm.add(new JLabel("Acta de asignación:"));
            initialForm.add(act);

            int option = JOptionPane.showConfirmDialog(this, initialForm,
                    "Asignar jurados - " + id, JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            int quantity = count.getSelectedIndex() + 1;
            List<Jury> juries = new ArrayList<>();

            for (int i = 0; i < quantity; i++) {
                JTextField juryId = new JTextField();
                JTextField lastNames = new JTextField();
                JTextField firstNames = new JTextField();
                JTextField email = new JTextField();
                JTextField phone = new JTextField();
                JCheckBox external = new JCheckBox("Es externo");
                JTextField institution = new JTextField();

                JPanel juryForm = new JPanel(new GridLayout(7, 2, 10, 10));
                juryForm.add(new JLabel("Identificación:"));
                juryForm.add(juryId);
                juryForm.add(new JLabel("Apellidos:"));
                juryForm.add(lastNames);
                juryForm.add(new JLabel("Nombres:"));
                juryForm.add(firstNames);
                juryForm.add(new JLabel("Correo:"));
                juryForm.add(email);
                juryForm.add(new JLabel("Teléfono:"));
                juryForm.add(phone);
                juryForm.add(new JLabel("Tipo:"));
                juryForm.add(external);
                juryForm.add(new JLabel("Institución (si es externo):"));
                juryForm.add(institution);

                int juryOption = JOptionPane.showConfirmDialog(this, juryForm,
                        "Datos del jurado " + (i + 1),
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (juryOption != JOptionPane.OK_OPTION) {
                    return;
                }

                juries.add(new Jury(
                        juryId.getText().trim(), lastNames.getText().trim(),
                        firstNames.getText().trim(), email.getText().trim(),
                        phone.getText().trim(), external.isSelected(),
                        external.isSelected() ? institution.getText().trim() : null));
            }

            int confirmation = JOptionPane.showConfirmDialog(this,
                    "¿Guardar la asignación de " + quantity + " jurado(s) al expediente "
                            + id + "?",
                    "Confirmar asignación", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }

            documentController.asignarJurados(id, act.getText().trim(), juries);
            JOptionPane.showMessageDialog(this,
                    "Jurados asignados correctamente.\n"
                            + "Puede consultarlos desde 'Consultar por ID'.",
                    "Asignación de jurados", JOptionPane.INFORMATION_MESSAGE);
        } catch (BusinessException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    private void updateObservations() {
        String id = txtId.getText().trim();

        try {
            Document document = documentController.buscarDocumentoPorId(id);
            if (document == null) {
                showError("No existe un expediente con ese identificador.");
                return;
            }
            if (document.getDegreeWork() == null) {
                showError("Solo se pueden actualizar las observaciones de un trabajo de grado aprobado.");
                return;
            }

            String previous = document.getDegreeWork().getObservations();
            JTextArea text = new JTextArea(previous == null ? "" : previous, 8, 40);
            text.setLineWrap(true);
            text.setWrapStyleWord(true);

            JPanel form = new JPanel(new BorderLayout(8, 8));
            form.add(new JLabel("Observaciones de seguimiento del trabajo:"), BorderLayout.NORTH);
            form.add(new JScrollPane(text), BorderLayout.CENTER);

            int option = JOptionPane.showConfirmDialog(this, form,
                    "Actualizar observaciones - " + id,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            String observations = text.getText().trim();
            int confirmation = JOptionPane.showConfirmDialog(this,
                    observations.isEmpty()
                            ? "El texto está vacío. ¿Desea borrar las observaciones de seguimiento?"
                            : "¿Guardar las observaciones de seguimiento del expediente " + id
                            + "?\nLas observaciones anteriores serán reemplazadas.",
                    "Confirmar cambio", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }

            documentController.actualizarObservaciones(id, observations);
            JOptionPane.showMessageDialog(this,
                    "Observaciones de seguimiento actualizadas correctamente.",
                    "Observaciones", JOptionPane.INFORMATION_MESSAGE);
        } catch (BusinessException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }
    private void addAttachment() {
        String id = txtId.getText().trim();

        try {
            Document document = documentController.buscarDocumentoPorId(id);
            if (document == null) {
                showError("No existe un expediente con ese identificador.");
                return;
            }

            JTextField name = new JTextField();
            JTextField type = new JTextField();
            JTextField path = new JTextField();
            JTextField date = new JTextField(LocalDate.now().toString());
            JButton browseButton = new JButton("Buscar...");

            browseButton.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    path.setText(file.getAbsolutePath());
                    if (name.getText().trim().isEmpty()) {
                        name.setText(file.getName());
                    }
                }
            });

            JPanel pathPanel = new JPanel(new BorderLayout(5, 0));
            pathPanel.add(path, BorderLayout.CENTER);
            pathPanel.add(browseButton, BorderLayout.EAST);

            JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
            form.add(new JLabel("Nombre del anexo:"));
            form.add(name);
            form.add(new JLabel("Tipo de documento:"));
            form.add(type);
            form.add(new JLabel("Ruta del archivo:"));
            form.add(pathPanel);
            form.add(new JLabel("Fecha de carga (AAAA-MM-DD):"));
            form.add(date);

            int option = JOptionPane.showConfirmDialog(this, form,
                    "Agregar anexo - " + id,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            Attachment attachment = new Attachment(
                    name.getText().trim(),
                    type.getText().trim(),
                    path.getText().trim(),
                    LocalDate.parse(date.getText().trim()));

            documentController.agregarAnexo(id, attachment);
            JOptionPane.showMessageDialog(this,
                    "Anexo agregado correctamente.\nPuede consultarlo desde 'Consultar por ID'.",
                    "Anexos", JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException e) {
            showError("La fecha debe tener el formato AAAA-MM-DD y ser válida.");
        } catch (BusinessException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }
    private void updateDocument() {
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

            int confirmation = JOptionPane.showConfirmDialog(this,
                    "¿Confirma la actualización de la propuesta " + document.getId() + "?",
                    "Confirmar actualización", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }

            documentController.actualizarDocumento(document);
            JOptionPane.showMessageDialog(this, "Propuesta actualizada correctamente.");
        } catch (DateTimeParseException e) {
            showError("La fecha debe tener el formato AAAA-MM-DD y ser válida.");
        } catch (BusinessException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    private void deleteDocument() {
        String id = txtId.getText().trim();

        try {
            Document document = documentController.buscarDocumentoPorId(id);
            if (document == null) {
                showError("No existe un expediente con ese identificador.");
                return;
            }

            int confirmation = JOptionPane.showConfirmDialog(this,
                    "¿Confirma que desea eliminar la propuesta " + id + "?\n"
                            + "Esta acción no se puede deshacer.",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }

            documentController.eliminarDocumento(id);
            JOptionPane.showMessageDialog(this, "Propuesta eliminada correctamente.");
            clearFields();
        } catch (BusinessException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }
    
    private void listAllDocuments() {
        try {
            List<Document> documents = documentController.listarDocumentos();
            if (documents.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay expedientes registrados.");
                return;
            }

            StringBuilder message = new StringBuilder();
            for (Document document : documents) {
                message.append("Expediente: ").append(document.getId()).append("\n");
                message.append("Título: ").append(document.getTitle()).append("\n");
                message.append("Modalidad: ").append(document.getModality().getDisplayName()).append("\n");
                message.append("Estudiantes: ").append(document.getStudentIds()).append("\n");
                message.append("Director: ").append(document.getDirectorId()).append("\n");
                message.append("Etapa: ").append(document.getStage().getDisplayName()).append("\n");
                message.append("Estado: ").append(document.getStatus().getDisplayName()).append("\n\n");
            }

            JTextArea report = new JTextArea(message.toString(), 18, 48);
            report.setEditable(false);
            report.setLineWrap(true);
            report.setWrapStyleWord(true);
            report.setCaretPosition(0);

            JOptionPane.showMessageDialog(this, new JScrollPane(report),
                    "Consulta general de expedientes", JOptionPane.INFORMATION_MESSAGE);
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
