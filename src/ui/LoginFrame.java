package ui;

import service.AuthService;
import service.ThemeService;
import dao.UsuarioDAO;
import model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;
    
    public LoginFrame() {
        setTitle("- Iniciar Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Configurar look and feel moderno
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initComponents();
    }
    
    private void initComponents() {
        // Panel principal con layout moderno
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Panel superior con título
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeService.COLOR_PRIMARIO);
        headerPanel.setPreferredSize(new Dimension(500, 120));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        
        JLabel lblTitle = new JLabel("BiblioMasters", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        
        JLabel lblSubtitle = new JLabel("Sistema de Gestión de Biblioteca", JLabel.CENTER);
        lblSubtitle.setFont(ThemeService.fuenteNormal());
        lblSubtitle.setForeground(new Color(240, 240, 240));
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        
        // Panel central con formulario
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 30, 50));
        
        // Título del formulario
        JLabel lblLoginTitle = new JLabel("Iniciar Sesión");
        lblLoginTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblLoginTitle.setForeground(ThemeService.COLOR_OSCURO);
        lblLoginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLoginTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Panel de email
        JPanel emailPanel = new JPanel(new BorderLayout(0, 5));
        emailPanel.setBackground(Color.WHITE);
        emailPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(ThemeService.fuenteSubtitulo());
        lblEmail.setForeground(ThemeService.COLOR_OSCURO);
        
        txtEmail = new JTextField();
        txtEmail.setFont(ThemeService.fuenteNormal());
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ThemeService.COLOR_CLARO),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        txtEmail.setBackground(new Color(250, 250, 250));
        
        emailPanel.add(lblEmail, BorderLayout.NORTH);
        emailPanel.add(txtEmail, BorderLayout.CENTER);
        
        // Panel de contraseña
        JPanel passwordPanel = new JPanel(new BorderLayout(0, 5));
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(ThemeService.fuenteSubtitulo());
        lblPassword.setForeground(ThemeService.COLOR_OSCURO);
        
        txtPassword = new JPasswordField();
        txtPassword.setFont(ThemeService.fuenteNormal());
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ThemeService.COLOR_CLARO),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        txtPassword.setBackground(new Color(250, 250, 250));
        
        passwordPanel.add(lblPassword, BorderLayout.NORTH);
        passwordPanel.add(txtPassword, BorderLayout.CENTER);
        
        // Botón de login
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setBackground(ThemeService.COLOR_PRIMARIO);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(400, 50));
        
        // Botón de registro
        btnRegister = new JButton("Registrar Nuevo Usuario");
        btnRegister.setFont(ThemeService.fuenteNormal());
        btnRegister.setBackground(Color.WHITE);
        btnRegister.setForeground(ThemeService.COLOR_PRIMARIO);
        btnRegister.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_PRIMARIO, 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(400, 45));
        
        // Agregar componentes al formulario
        formPanel.add(lblLoginTitle);
        formPanel.add(emailPanel);
        formPanel.add(passwordPanel);
        formPanel.add(btnLogin);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(btnRegister);
        
        // Agregar todos los paneles al principal
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Configurar acciones de botones (MANTENIENDO LA LÓGICA ORIGINAL)
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarUsuario();
            }
        });
        
        // Enter para login
        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
    }
    
    // MÉTODOS ORIGINALES PRESERVADOS - SIN CAMBIOS EN LA LÓGICA
    
    private void login() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa email y contraseña", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (AuthService.login(email, password)) {
            // Login exitoso - abrir panel correspondiente
            abrirPanelUsuario();
        } else {
            JOptionPane.showMessageDialog(this, "Email o contraseña incorrectos", "Error de login", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void registrarUsuario() {
        // Crear un diálogo de registro modernizado
        JDialog registerDialog = new JDialog(this, "Registrar Nuevo Usuario", true);
        registerDialog.setSize(500, 750); 
        registerDialog.setLocationRelativeTo(this);
        registerDialog.setResizable(false);
        
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        dialogPanel.setBackground(Color.WHITE);
        
        // Título
        JLabel titleLabel = new JLabel("Registrar Nuevo Usuario");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ThemeService.COLOR_OSCURO);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        
        // Campos del formulario
        JTextField txtNombre = crearCampoTexto("Nombre completo");
        JTextField txtEmail = crearCampoTexto("Email");
        JPasswordField txtPassword = crearCampoPassword("Contraseña");
        JPasswordField txtConfirmPassword = crearCampoPassword("Confirmar contraseña");
        JTextField txtCodigo = crearCampoTexto("Código de identificación");
        
        JComboBox<String> cmbTipoUsuario = new JComboBox<>(new String[]{
            "ESTUDIANTE", "DOCENTE", "BIBLIOTECARIO"
        });
        cmbTipoUsuario.setFont(ThemeService.fuenteNormal());
        cmbTipoUsuario.setBackground(Color.WHITE);
        cmbTipoUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ThemeService.COLOR_CLARO),
            BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));
        
        // Panel para tipo de usuario
        JPanel tipoPanel = new JPanel(new BorderLayout(0, 5));
        tipoPanel.setBackground(Color.WHITE);
        JLabel lblTipo = new JLabel("Tipo de usuario:");
        lblTipo.setFont(ThemeService.fuenteSubtitulo());
        lblTipo.setForeground(ThemeService.COLOR_OSCURO);
        tipoPanel.add(lblTipo, BorderLayout.NORTH);
        tipoPanel.add(cmbTipoUsuario, BorderLayout.CENTER);
        
        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton btnCancel = new JButton("Cancelar");
        btnCancel.setFont(ThemeService.fuenteNormal());
        btnCancel.setBackground(new Color(220, 220, 220));
        btnCancel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> registerDialog.dispose());
        
        JButton btnRegister = new JButton("Registrar");
        btnRegister.setFont(ThemeService.fuenteNormal());
        btnRegister.setBackground(ThemeService.COLOR_PRIMARIO);
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.addActionListener(e -> {
            procesarRegistro(txtNombre.getText().trim(), txtEmail.getText().trim(),
                           new String(txtPassword.getPassword()), 
                           new String(txtConfirmPassword.getPassword()),
                           (String) cmbTipoUsuario.getSelectedItem(),
                           txtCodigo.getText().trim(), registerDialog);
        });
        
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnRegister);
        
        // Agregar componentes al diálogo
        dialogPanel.add(titleLabel);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        dialogPanel.add(crearPanelCampo("Nombre completo", txtNombre));
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        dialogPanel.add(crearPanelCampo("Email", txtEmail));
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        dialogPanel.add(crearPanelCampo("Contraseña", txtPassword));
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        dialogPanel.add(crearPanelCampo("Confirmar contraseña", txtConfirmPassword));
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        dialogPanel.add(tipoPanel);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        dialogPanel.add(crearPanelCampo("Código de identificación", txtCodigo));
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        dialogPanel.add(buttonPanel);
        
        registerDialog.add(dialogPanel);
        registerDialog.setVisible(true);
    }
    
    // Métodos auxiliares para crear componentes estilizados
    private JTextField crearCampoTexto(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(ThemeService.fuenteNormal());
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ThemeService.COLOR_CLARO),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        field.setBackground(new Color(250, 250, 250));
        return field;
    }
    
    private JPasswordField crearCampoPassword(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(ThemeService.fuenteNormal());
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ThemeService.COLOR_CLARO),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        field.setBackground(new Color(250, 250, 250));
        return field;
    }
    
    private JPanel crearPanelCampo(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeService.fuenteSubtitulo());
        lbl.setForeground(ThemeService.COLOR_OSCURO);
        
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void procesarRegistro(String nombre, String email, String password, 
                                String confirmPassword, String tipoUsuario, 
                                String codigo, JDialog dialog) {
        // Validaciones (igual que el original)
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Email no válido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 4 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Crear y guardar usuario
        Usuario nuevoUsuario = new Usuario(nombre, email, password, tipoUsuario, codigo);
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        
        if (usuarioDAO.agregarUsuario(nuevoUsuario)) {
            JOptionPane.showMessageDialog(this, 
                "Usuario registrado exitosamente!\n\n" +
                "Nombre: " + nombre + "\n" +
                "Email: " + email + "\n" +
                "Tipo: " + tipoUsuario + "\n\n" +
                "Ya puedes iniciar sesión con tu email y contraseña.", 
                "Registro Exitoso", 
                JOptionPane.INFORMATION_MESSAGE);
                
            dialog.dispose();
            
            // Limpiar campos después de registro exitoso
            txtEmail.setText("");
            txtPassword.setText("");
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error al registrar usuario.\n" +
                "Posibles causas:\n" +
                "- El email ya existe\n" +
                "- El código ya está en uso", 
                "Error de Registro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void abrirPanelUsuario() {
        // Cerrar login
        dispose();
        
        // Abrir panel según tipo de usuario
        if (AuthService.esAdministrador()) {
            new AdminPanel().setVisible(true);
        } else if (AuthService.esBibliotecario()) {
            new BibliotecaPanel().setVisible(true);
        } else if (AuthService.esEstudiante() || AuthService.esDocente()) {
            new UsuarioPanel().setVisible(true);
        }
    }
    
    public static void main(String[] args) {
        // Mostrar login
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}