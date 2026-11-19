package co.edu.uptc.view;

import co.edu.uptc.controller.StudentController;
import co.edu.uptc.exception.BusinessException;
import co.edu.uptc.model.Student;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class StudentView extends JFrame {

    private final StudentController studentController;
    private final JTextField txtCode;
    private final JTextField txtIdNumber;
    private final JTextField txtFirstNames;
    private final JTextField txtLastNames;
    private final JTextField txtEmail;
    private final JTextField txtPhone;

    public StudentView(StudentController studentController) {
        this.studentController = studentController;

        setTitle("Gestión de estudiantes");
        setSize(530, 360);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(content);

        txtCode = new JTextField();
        txtIdNumber = new JTextField();
        txtFirstNames = new JTextField();
        txtLastNames = new JTextField();
        txtEmail = new JTextField();
        txtPhone = new JTextField();

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.add(new JLabel("Código estudiantil:"));
        form.add(txtCode);
        form.add(new JLabel("Cédula:"));
        form.add(txtIdNumber);
        form.add(new JLabel("Nombres:"));
        form.add(txtFirstNames);
        form.add(new JLabel("Apellidos:"));
        form.add(txtLastNames);
        form.add(new JLabel("Correo electrónico:"));
        form.add(txtEmail);
        form.add(new JLabel("Teléfono:"));
        form.add(txtPhone);
        content.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(2, 3, 10, 10));
        JButton registerButton = new JButton("Registrar");
        JButton searchButton = new JButton("Buscar por código");
        JButton updateButton = new JButton("Actualizar");
        JButton deleteButton = new JButton("Eliminar");
        JButton clearButton = new JButton("Limpiar");
        buttons.add(registerButton);
        buttons.add(searchButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);
        content.add(buttons, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> registerStudent());
        searchButton.addActionListener(e -> searchStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        clearButton.addActionListener(e -> clearFields());
    }

    private void registerStudent() {
        Student student = new Student(
                txtIdNumber.getText().trim(),
                txtCode.getText().trim(),
                txtLastNames.getText().trim(),
                txtFirstNames.getText().trim(),
                txtEmail.getText().trim(),
                txtPhone.getText().trim());

        try {
            studentController.registrarEstudiante(student);
            JOptionPane.showMessageDialog(this, "Estudiante registrado correctamente.");
            clearFields();
        } catch (BusinessException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    private void searchStudent() {
        try {
            Student student = studentController.buscarEstudiantePorCodigo(txtCode.getText().trim());
            if (student == null) {
                JOptionPane.showMessageDialog(this, "No se encontró un estudiante con ese código.");
                return;
            }

            txtIdNumber.setText(student.getIdNumber());
            txtCode.setText(student.getCode());
            txtFirstNames.setText(student.getFirstNames());
            txtLastNames.setText(student.getLastNames());
            txtEmail.setText(student.getEmail());
            txtPhone.setText(student.getPhone());
        } catch (BusinessException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }
    private void updateStudent() {
        Student student = new Student(
                txtIdNumber.getText().trim(),
                txtCode.getText().trim(),
                txtLastNames.getText().trim(),
                txtFirstNames.getText().trim(),
                txtEmail.getText().trim(),
                txtPhone.getText().trim());

        try {
            int confirmation = JOptionPane.showConfirmDialog(this,
                    "¿Confirma la actualización del estudiante con código " + student.getCode() + "?",
                    "Confirmar actualización", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }

            studentController.actualizarEstudiante(student);
            JOptionPane.showMessageDialog(this, "Estudiante actualizado correctamente.");
        } catch (BusinessException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    private void deleteStudent() {
        String code = txtCode.getText().trim();

        try {
            Student student = studentController.buscarEstudiantePorCodigo(code);
            if (student == null) {
                showError("No se encontró un estudiante con ese código.");
                return;
            }

            int confirmation = JOptionPane.showConfirmDialog(this,
                    "¿Confirma que desea eliminar al estudiante " + student.getFirstNames()
                            + " " + student.getLastNames() + " (" + code + ")?",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }

            studentController.eliminarEstudiante(code);
            JOptionPane.showMessageDialog(this, "Estudiante eliminado correctamente.");
            clearFields();
        } catch (BusinessException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    private void clearFields() {
        txtCode.setText("");
        txtIdNumber.setText("");
        txtFirstNames.setText("");
        txtLastNames.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtCode.requestFocus();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
