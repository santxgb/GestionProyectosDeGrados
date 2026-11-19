package co.edu.uptc.view;

import co.edu.uptc.controller.TeacherController;
import co.edu.uptc.exception.BusinessException;
import co.edu.uptc.model.Teacher;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class TeacherView extends JFrame {

    private final TeacherController teacherController;
    private final JTextField txtId;
    private final JTextField txtFirstNames;
    private final JTextField txtLastNames;
    private final JTextField txtEmail;
    private final JTextField txtPhone;

    public TeacherView(TeacherController teacherController) {
        this.teacherController = teacherController;

        setTitle("Gestión de docentes");
        setSize(530, 330);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(content);

        txtId = new JTextField();
        txtFirstNames = new JTextField();
        txtLastNames = new JTextField();
        txtEmail = new JTextField();
        txtPhone = new JTextField();

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.add(new JLabel("Identificación del docente:"));
        form.add(txtId);
        form.add(new JLabel("Nombres:"));
        form.add(txtFirstNames);
        form.add(new JLabel("Apellidos:"));
        form.add(txtLastNames);
        form.add(new JLabel("Correo electrónico:"));
        form.add(txtEmail);
        form.add(new JLabel("Teléfono:"));
        form.add(txtPhone);
        content.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton registerButton = new JButton("Registrar");
        JButton searchButton = new JButton("Buscar por ID");
        JButton clearButton = new JButton("Limpiar");
        buttons.add(registerButton);
        buttons.add(searchButton);
        buttons.add(clearButton);
        content.add(buttons, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> registerTeacher());
        searchButton.addActionListener(e -> searchTeacher());
        clearButton.addActionListener(e -> clearFields());
    }

    private void registerTeacher() {
        Teacher teacher = new Teacher(
                txtId.getText().trim(),
                txtLastNames.getText().trim(),
                txtFirstNames.getText().trim(),
                txtEmail.getText().trim(),
                txtPhone.getText().trim());

        try {
            teacherController.registrarDocente(teacher);
            JOptionPane.showMessageDialog(this, "Docente registrado correctamente.");
            clearFields();
        } catch (BusinessException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    private void searchTeacher() {
        try {
            Teacher teacher = teacherController.buscarProfesorPorId(txtId.getText().trim());
            if (teacher == null) {
                JOptionPane.showMessageDialog(this, "No se encontró un docente con esa identificación.");
                return;
            }

            txtId.setText(teacher.getId());
            txtFirstNames.setText(teacher.getFirstNames());
            txtLastNames.setText(teacher.getLastNames());
            txtEmail.setText(teacher.getEmail());
            txtPhone.setText(teacher.getPhone());
        } catch (BusinessException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtFirstNames.setText("");
        txtLastNames.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtId.requestFocus();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
