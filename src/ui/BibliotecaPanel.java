package ui;

import service.AuthService;
import service.ThemeService;
import dao.LibroDAO;
import dao.PrestamoDAO;
import dao.UsuarioDAO;
import model.Libro;
import model.Prestamo;
import model.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class BibliotecaPanel extends JFrame {
    private JTable tablaPrestamosActivos;
    private DefaultTableModel modeloPrestamosActivos;
    private JTable tablaLibrosDisponibles;
    private DefaultTableModel modeloLibrosDisponibles;
    
    public BibliotecaPanel() {
        setTitle("BiblioMasters - Panel de Bibliotecario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        if (!AuthService.estaLogueado() || !AuthService.esBibliotecario()) {
            JOptionPane.showMessageDialog(this, "Acceso denegado", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeService.COLOR_PRIMARIO);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        headerPanel.setPreferredSize(new Dimension(1200, 80));
        
        JLabel lblTitulo = new JLabel("BiblioMasters");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setBackground(ThemeService.COLOR_PRIMARIO);
        
        JLabel lblUsuario = new JLabel(AuthService.getUsuarioLogueado().getNombre() + " (Bibliotecario)");
        lblUsuario.setFont(ThemeService.fuenteNormal());
        lblUsuario.setForeground(Color.WHITE);
        
        JButton btnLogout = crearBotonHeader("Salir", ThemeService.COLOR_PRIMARIO);
        JButton btnCambiarUsuario = crearBotonHeader("Cambiar", ThemeService.COLOR_PRIMARIO);
        
        userPanel.add(lblUsuario);
        userPanel.add(btnCambiarUsuario);
        userPanel.add(btnLogout);
        
        headerPanel.add(lblTitulo, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeService.COLOR_CLARO),
            BorderFactory.createEmptyBorder(20, 0, 20, 0)
        ));
        navPanel.setPreferredSize(new Dimension(200, 0));
        
        String[] opcionesNav = {"Prestamos", "Nuevo Prestamo", "Devoluciones", "Inventario", "Salir"};
        for (String opcion : opcionesNav) {
            JButton btnNav = crearBotonNavegacion(opcion);
            navPanel.add(btnNav);
            navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        JPanel contentPanel = crearPanelPrestamosActivos();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, navPanel, contentPanel);
        splitPane.setDividerLocation(200);
        splitPane.setDividerSize(2);
        splitPane.setEnabled(false);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        btnLogout.addActionListener(e -> logout());
        btnCambiarUsuario.addActionListener(e -> cambiarUsuario());
        
        configurarNavegacion(navPanel, splitPane);
    }
    
    private void configurarNavegacion(JPanel navPanel, JSplitPane splitPane) {
        Component[] componentes = navPanel.getComponents();
        for (Component comp : componentes) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.addActionListener(e -> {
                    String texto = btn.getText();
                    switch (texto) {
                        case "Prestamos":
                            splitPane.setRightComponent(crearPanelPrestamosActivos());
                            break;
                        case "Nuevo Prestamo":
                            splitPane.setRightComponent(crearPanelNuevoPrestamo());
                            break;
                        case "Devoluciones":
                            splitPane.setRightComponent(crearPanelDevoluciones());
                            break;
                        case "Inventario":
                            splitPane.setRightComponent(crearPanelLibrosDisponibles());
                            cargarLibrosDisponibles();
                            break;
                        case "Salir":
                            logout();
                            break;
                    }
                    splitPane.revalidate();
                    splitPane.repaint();
                });
            }
        }
    }
    
    private JButton crearBotonHeader(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(ThemeService.fuentePequeña());
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }
    
    private JButton crearBotonNavegacion(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(ThemeService.fuenteNormal());
        boton.setBackground(Color.WHITE);
        boton.setForeground(ThemeService.COLOR_OSCURO);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setMaximumSize(new Dimension(200, 45));
        
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(220, 240, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(Color.WHITE);
            }
        });
        
        return boton;
    }
    
    private JButton crearBotonAccion(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(ThemeService.fuenteNormal());
        boton.setBackground(ThemeService.COLOR_PRIMARIO);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }
    
    private JPanel crearPanelPrestamosActivos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblSeccionTitulo = new JLabel("Prestamos Activos");
        lblSeccionTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblSeccionTitulo.setForeground(ThemeService.COLOR_OSCURO);
        lblSeccionTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JTextField txtBusqueda = new JTextField();
        txtBusqueda.setFont(ThemeService.fuenteNormal());
        txtBusqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        txtBusqueda.setText("Buscar por libro, usuario o fecha...");
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(ThemeService.fuenteNormal());
        btnBuscar.setBackground(ThemeService.COLOR_PRIMARIO);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnBuscar.setFocusPainted(false);
        
        searchPanel.add(txtBusqueda, BorderLayout.CENTER);
        searchPanel.add(btnBuscar, BorderLayout.EAST);
        
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JButton btnActualizar = crearBotonAccion("Actualizar");
        JButton btnVerDetalles = crearBotonAccion("Ver Detalles");
        
        actionsPanel.add(btnActualizar);
        actionsPanel.add(btnVerDetalles);
        
        String[] columnas = {"ID", "Libro", "Usuario", "Fecha Prestamo", "Fecha Devolucion", "Dias Restantes", "Estado"};
        modeloPrestamosActivos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaPrestamosActivos = new JTable(modeloPrestamosActivos);
        tablaPrestamosActivos.setFont(ThemeService.fuenteNormal());
        tablaPrestamosActivos.setRowHeight(35);
        tablaPrestamosActivos.setSelectionBackground(new Color(220, 240, 255));
        tablaPrestamosActivos.setSelectionForeground(ThemeService.COLOR_OSCURO);
        tablaPrestamosActivos.setGridColor(ThemeService.COLOR_CLARO);
        
        JScrollPane scrollPane = new JScrollPane(tablaPrestamosActivos);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        panel.add(lblSeccionTitulo, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        btnActualizar.addActionListener(e -> cargarPrestamosActivos());
        btnVerDetalles.addActionListener(e -> verDetallesPrestamo());
        btnBuscar.addActionListener(e -> {
            buscarPrestamos(txtBusqueda.getText().trim());
        });
        
        return panel;
    }
    
    private JPanel crearPanelNuevoPrestamo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        JLabel lblTitulo = new JLabel("Registrar Nuevo Prestamo");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(ThemeService.COLOR_OSCURO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        JPanel libroPanel = new JPanel(new BorderLayout(10, 5));
        libroPanel.setBackground(Color.WHITE);
        libroPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel lblLibro = new JLabel("ID del Libro:");
        lblLibro.setFont(ThemeService.fuenteSubtitulo());
        lblLibro.setForeground(ThemeService.COLOR_OSCURO);
        
        JTextField txtIdLibro = new JTextField();
        txtIdLibro.setFont(ThemeService.fuenteNormal());
        txtIdLibro.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        libroPanel.add(lblLibro, BorderLayout.NORTH);
        libroPanel.add(txtIdLibro, BorderLayout.CENTER);
        
        JPanel usuarioPanel = new JPanel(new BorderLayout(10, 5));
        usuarioPanel.setBackground(Color.WHITE);
        usuarioPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel lblUsuario = new JLabel("ID del Usuario:");
        lblUsuario.setFont(ThemeService.fuenteSubtitulo());
        lblUsuario.setForeground(ThemeService.COLOR_OSCURO);
        
        JTextField txtIdUsuario = new JTextField();
        txtIdUsuario.setFont(ThemeService.fuenteNormal());
        txtIdUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        usuarioPanel.add(lblUsuario, BorderLayout.NORTH);
        usuarioPanel.add(txtIdUsuario, BorderLayout.CENTER);
        
        JPanel fechaPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        fechaPanel.setBackground(Color.WHITE);
        fechaPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        LocalDate hoy = LocalDate.now();
        LocalDate fechaDevolucion = hoy.plusDays(15);
        
        JLabel lblFechaPrestamo = new JLabel("Fecha de Prestamo:");
        lblFechaPrestamo.setFont(ThemeService.fuenteSubtitulo());
        JTextField txtFechaPrestamo = new JTextField(hoy.toString());
        txtFechaPrestamo.setFont(ThemeService.fuenteNormal());
        txtFechaPrestamo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtFechaPrestamo.setEditable(false);
        
        JLabel lblFechaDevolucion = new JLabel("Fecha de Devolucion Estimada:");
        lblFechaDevolucion.setFont(ThemeService.fuenteSubtitulo());
        JTextField txtFechaDevolucion = new JTextField(fechaDevolucion.toString());
        txtFechaDevolucion.setFont(ThemeService.fuenteNormal());
        txtFechaDevolucion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtFechaDevolucion.setEditable(false);
        
        fechaPanel.add(lblFechaPrestamo);
        fechaPanel.add(txtFechaPrestamo);
        fechaPanel.add(lblFechaDevolucion);
        fechaPanel.add(txtFechaDevolucion);
        
        JButton btnRegistrar = crearBotonAccion("Registrar Prestamo");
        btnRegistrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegistrar.setMaximumSize(new Dimension(300, 50));
        
        panel.add(lblTitulo);
        panel.add(libroPanel);
        panel.add(usuarioPanel);
        panel.add(fechaPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(btnRegistrar);
        
        btnRegistrar.addActionListener(e -> {
            registrarPrestamo(txtIdLibro.getText().trim(), txtIdUsuario.getText().trim(), 
                            txtFechaPrestamo.getText(), txtFechaDevolucion.getText());
        });
        
        return panel;
    }
    
    private void registrarPrestamo(String idLibroStr, String idUsuarioStr, String fechaPrestamo, String fechaDevolucion) {
        if (idLibroStr.isEmpty() || idUsuarioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ERROR: Por favor ingresa ID del libro y ID del usuario", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int idLibro = Integer.parseInt(idLibroStr);
            int idUsuario = Integer.parseInt(idUsuarioStr);
            
            LibroDAO libroDAO = new LibroDAO();
            Libro libro = libroDAO.buscarPorId(idLibro);
            
            if (libro == null) {
                JOptionPane.showMessageDialog(this, "ERROR: Libro no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!"DISPONIBLE".equals(libro.getEstado())) {
                JOptionPane.showMessageDialog(this, 
                    "ERROR: El libro no esta disponible para prestamo\n" +
                    "Estado actual: " + libro.getEstado(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
            
            if (usuario == null) {
                JOptionPane.showMessageDialog(this, "ERROR: Usuario no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!usuario.esEstudiante() && !usuario.esDocente() && !usuario.esBibliotecario()) {
                JOptionPane.showMessageDialog(this, 
                    "ERROR: Este tipo de usuario no puede solicitar prestamos\n" +
                    "Tipo: " + usuario.getTipoUsuario(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Prestamo nuevoPrestamo = new Prestamo();
            nuevoPrestamo.setIdUsuario(idUsuario);
            nuevoPrestamo.setIdLibro(idLibro);
            nuevoPrestamo.setFechaPrestamo(fechaPrestamo);
            nuevoPrestamo.setFechaDevolucionEstimada(fechaDevolucion);
            nuevoPrestamo.setEstado("ACTIVO");
            
            PrestamoDAO prestamoDAO = new PrestamoDAO();
            
            if (prestamoDAO.crearPrestamo(nuevoPrestamo)) {
                JOptionPane.showMessageDialog(this, 
                    "EXITO: Prestamo registrado exitosamente!\n\n" +
                    "Libro: " + libro.getTitulo() + "\n" +
                    "Usuario: " + usuario.getNombre() + "\n" +
                    "Fecha Devolucion: " + fechaDevolucion + "\n" +
                    "Tipo Usuario: " + usuario.getTipoUsuario(),
                    "Prestamo Exitoso", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarPrestamosActivos();
                
            } else {
                JOptionPane.showMessageDialog(this, 
                    "ERROR: Error al registrar el prestamo\n" +
                    "Posible causa: El libro ya no esta disponible", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "ERROR: Los IDs deben ser numeros validos", 
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "ERROR: Error inesperado: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel crearPanelDevoluciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        JLabel lblTitulo = new JLabel("Devoluciones");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(ThemeService.COLOR_OSCURO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        JPanel scanPanel = new JPanel(new BorderLayout(10, 5));
        scanPanel.setBackground(Color.WHITE);
        scanPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel lblScan = new JLabel("Ingresar ID del prestamo:");
        lblScan.setFont(ThemeService.fuenteSubtitulo());
        lblScan.setForeground(ThemeService.COLOR_OSCURO);
        
        JTextField txtIdPrestamo = new JTextField();
        txtIdPrestamo.setFont(ThemeService.fuenteNormal());
        txtIdPrestamo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(ThemeService.fuenteNormal());
        btnBuscar.setBackground(ThemeService.COLOR_PRIMARIO);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnBuscar.setFocusPainted(false);
        
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.add(txtIdPrestamo, BorderLayout.CENTER);
        inputPanel.add(btnBuscar, BorderLayout.EAST);
        
        scanPanel.add(lblScan, BorderLayout.NORTH);
        scanPanel.add(inputPanel, BorderLayout.CENTER);
        
        JPanel detallesPanel = new JPanel();
        detallesPanel.setLayout(new BoxLayout(detallesPanel, BoxLayout.Y_AXIS));
        detallesPanel.setBackground(new Color(240, 245, 250));
        detallesPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        detallesPanel.setVisible(false);
        
        JLabel lblDetalles = new JLabel("Detalles del Prestamo");
        lblDetalles.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDetalles.setForeground(ThemeService.COLOR_OSCURO);
        
        JLabel lblLibro = new JLabel("Libro: ");
        JLabel lblUsuario = new JLabel("Usuario: ");
        JLabel lblFechaPrestamo = new JLabel("Fecha Prestamo: ");
        JLabel lblFechaDevolucion = new JLabel("Fecha Devolucion: ");
        JLabel lblEstado = new JLabel("Estado: ");
        
        detallesPanel.add(lblDetalles);
        detallesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detallesPanel.add(lblLibro);
        detallesPanel.add(lblUsuario);
        detallesPanel.add(lblFechaPrestamo);
        detallesPanel.add(lblFechaDevolucion);
        detallesPanel.add(lblEstado);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton btnConfirmar = crearBotonAccion("Confirmar Devolucion");
        btnConfirmar.setVisible(false);
        
        buttonPanel.add(btnConfirmar);
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(scanPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(detallesPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(buttonPanel);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        btnBuscar.addActionListener(e -> {
            buscarPrestamoParaDevolucion(txtIdPrestamo.getText().trim(), detallesPanel, btnConfirmar, lblLibro, lblUsuario, lblFechaPrestamo, lblFechaDevolucion, lblEstado);
        });
        
        btnConfirmar.addActionListener(e -> {
            confirmarDevolucion(txtIdPrestamo.getText().trim());
        });
        
        return panel;
    }
    
    private void buscarPrestamoParaDevolucion(String idPrestamoStr, JPanel detallesPanel, JButton btnConfirmar,
                                            JLabel lblLibro, JLabel lblUsuario, JLabel lblFechaPrestamo,
                                            JLabel lblFechaDevolucion, JLabel lblEstado) {
        if (idPrestamoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ERROR: Por favor ingresa un ID de prestamo", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int idPrestamo = Integer.parseInt(idPrestamoStr);
            PrestamoDAO prestamoDAO = new PrestamoDAO();
            Prestamo prestamo = prestamoDAO.buscarPorId(idPrestamo);
            
            if (prestamo == null) {
                JOptionPane.showMessageDialog(this, "ERROR: Prestamo no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                detallesPanel.setVisible(false);
                btnConfirmar.setVisible(false);
                return;
            }
            
            if (!"ACTIVO".equals(prestamo.getEstado())) {
                JOptionPane.showMessageDialog(this, "INFORMACION: Este prestamo ya fue devuelto", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                detallesPanel.setVisible(false);
                btnConfirmar.setVisible(false);
                return;
            }
            
            lblLibro.setText("Libro: " + prestamo.getTituloLibro());
            lblUsuario.setText("Usuario: " + prestamo.getNombreUsuario());
            lblFechaPrestamo.setText("Fecha Prestamo: " + prestamo.getFechaPrestamo());
            lblFechaDevolucion.setText("Fecha Devolucion: " + prestamo.getFechaDevolucionEstimada());
            lblEstado.setText("Estado: " + prestamo.getEstado());
            
            detallesPanel.setVisible(true);
            btnConfirmar.setVisible(true);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ERROR: El ID debe ser un numero valido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void confirmarDevolucion(String idPrestamoStr) {
        try {
            int idPrestamo = Integer.parseInt(idPrestamoStr);
            PrestamoDAO prestamoDAO = new PrestamoDAO();
            
            if (prestamoDAO.registrarDevolucion(idPrestamo)) {
                JOptionPane.showMessageDialog(this, "EXITO: Devolucion registrada exitosamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
                
                cargarPrestamosActivos();
                
                // Limpiar campos
                Component parent = getContentPane().getComponent(0);
                if (parent instanceof JPanel) {
                    for (Component comp : ((JPanel) parent).getComponents()) {
                        if (comp instanceof JPanel) {
                            Component[] subComps = ((JPanel) comp).getComponents();
                            for (Component subComp : subComps) {
                                if (subComp instanceof JTextField) {
                                    ((JTextField) subComp).setText("");
                                } else if (subComp instanceof JPanel) {
                                    subComp.setVisible(false);
                                }
                            }
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "ERROR: Error al registrar la devolucion", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ERROR: El ID debe ser un numero valido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel crearPanelLibrosDisponibles() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblSeccionTitulo = new JLabel("Inventario - Todos los Libros");
        lblSeccionTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblSeccionTitulo.setForeground(ThemeService.COLOR_OSCURO);
        lblSeccionTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Panel de busqueda y botones
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JTextField txtBusqueda = new JTextField();
        txtBusqueda.setFont(ThemeService.fuenteNormal());
        txtBusqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        txtBusqueda.setText("Buscar por titulo, autor, ISBN o QR...");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(ThemeService.fuenteNormal());
        btnBuscar.setBackground(ThemeService.COLOR_PRIMARIO);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnBuscar.setFocusPainted(false);
        
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setFont(ThemeService.fuenteNormal());
        btnActualizar.setBackground(ThemeService.COLOR_SECUNDARIO);
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buttonPanel.add(btnBuscar);
        buttonPanel.add(btnActualizar);
        
        topPanel.add(txtBusqueda, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        String[] columnas = {"ID", "Titulo", "Autor", "Categoria", "Editorial", "Anio", "Estado"};
        modeloLibrosDisponibles = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaLibrosDisponibles = new JTable(modeloLibrosDisponibles);
        tablaLibrosDisponibles.setFont(ThemeService.fuenteNormal());
        tablaLibrosDisponibles.setRowHeight(35);
        tablaLibrosDisponibles.setSelectionBackground(new Color(220, 240, 255));
        tablaLibrosDisponibles.setSelectionForeground(ThemeService.COLOR_OSCURO);
        tablaLibrosDisponibles.setGridColor(ThemeService.COLOR_CLARO);
        
        JScrollPane scrollPane = new JScrollPane(tablaLibrosDisponibles);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(lblSeccionTitulo, BorderLayout.NORTH);
        panel.add(topPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        // Configurar acciones
        btnBuscar.addActionListener(e -> {
            buscarLibros(txtBusqueda.getText().trim());
        });
        
        btnActualizar.addActionListener(e -> {
            cargarLibrosDisponibles();
            JOptionPane.showMessageDialog(this, "EXITO: Inventario actualizado", "Actualizado", JOptionPane.INFORMATION_MESSAGE);
        });
        
        return panel;
    }
    
    private void logout() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Estas seguro de que quieres cerrar sesion?",
            "Confirmar Cierre de Sesion",
            JOptionPane.YES_NO_OPTION);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            AuthService.logout();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    private void cambiarUsuario() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Quieres cambiar de usuario? Se cerrara la sesion actual.",
            "Cambiar Usuario",
            JOptionPane.YES_NO_OPTION);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            AuthService.logout();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    private void cargarDatos() {
        cargarPrestamosActivos();
    }
    
    private void cargarPrestamosActivos() {
        if (modeloPrestamosActivos != null) {
            modeloPrestamosActivos.setRowCount(0);
            
            PrestamoDAO prestamoDAO = new PrestamoDAO();
            List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosActivos();
            
            for (Prestamo prestamo : prestamos) {
                Object[] fila = {
                    prestamo.getIdPrestamo(),
                    prestamo.getTituloLibro(),
                    prestamo.getNombreUsuario(),
                    prestamo.getFechaPrestamo(),
                    prestamo.getFechaDevolucionEstimada(),
                    "3 Dias",
                    prestamo.getEstado()
                };
                modeloPrestamosActivos.addRow(fila);
            }
        }
    }
    
    private void cargarLibrosDisponibles() {
        if (modeloLibrosDisponibles != null) {
            modeloLibrosDisponibles.setRowCount(0);
            
            LibroDAO libroDAO = new LibroDAO();
            List<Libro> libros = libroDAO.obtenerTodosLibros();
            
            for (Libro libro : libros) {
                Object[] fila = {
                    libro.getIdLibro(),
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getCategoria(),
                    libro.getEditorial(),
                    libro.getAnioPublicacion(),
                    libro.getEstado()
                };
                modeloLibrosDisponibles.addRow(fila);
            }
            
            if (libros.isEmpty()) {
                Object[] filaPrueba = {
                    999,
                    "Libro de Prueba - Agrega libros en Admin",
                    "Sistema",
                    "General",
                    "Sistema",
                    2024,
                    "DISPONIBLE"
                };
                modeloLibrosDisponibles.addRow(filaPrueba);
            }
        }
    }
    
    private void verDetallesPrestamo() {
        if (tablaPrestamosActivos.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor selecciona un prestamo de la tabla", 
                "Advertencia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int filaSeleccionada = tablaPrestamosActivos.getSelectedRow();
            int idPrestamo = (int) modeloPrestamosActivos.getValueAt(filaSeleccionada, 0);
            
            PrestamoDAO prestamoDAO = new PrestamoDAO();
            Prestamo prestamo = prestamoDAO.buscarPorId(idPrestamo);
            
            if (prestamo == null) {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo encontrar la informacion del prestamo seleccionado", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String mensaje = String.format(
                "DETALLES DEL PRESTAMO #%d\n\n" +
                "INFORMACION DEL LIBRO:\n" +
                "• Titulo: %s\n" +
                "• ID del libro: %d\n\n" +
                "INFORMACION DEL USUARIO:\n" +
                "• Nombre: %s\n" +
                "• ID del usuario: %d\n\n" +
                "FECHAS:\n" +
                "• Fecha de prestamo: %s\n" +
                "• Fecha de devolucion estimada: %s\n" +
                "• Fecha de devolucion real: %s\n\n" +
                "ESTADO ACTUAL: %s",
                prestamo.getIdPrestamo(),
                prestamo.getTituloLibro(),
                prestamo.getIdLibro(),
                prestamo.getNombreUsuario(),
                prestamo.getIdUsuario(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucionEstimada(),
                (prestamo.getFechaDevolucionReal() != null ? prestamo.getFechaDevolucionReal() : "Pendiente"),
                prestamo.getEstado()
            );
            
            JTextArea textArea = new JTextArea(mensaje);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBackground(new Color(240, 240, 240));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));
            
            JOptionPane.showMessageDialog(this, 
                scrollPane, 
                "Detalles del Prestamo #" + prestamo.getIdPrestamo(), 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al obtener los detalles del prestamo: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarLibros(String textoBusqueda) {
        if (textoBusqueda.isEmpty() || textoBusqueda.equals("Buscar por titulo, autor, ISBN o QR...")) {
            cargarLibrosDisponibles();
            return;
        }
        
        try {
            modeloLibrosDisponibles.setRowCount(0);
            
            LibroDAO libroDAO = new LibroDAO();
            List<Libro> librosEncontrados = libroDAO.buscarLibros(textoBusqueda);
            
            if (librosEncontrados.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No se encontraron libros que coincidan con: " + textoBusqueda, 
                    "Busqueda sin resultados", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            for (Libro libro : librosEncontrados) {
                Object[] fila = {
                    libro.getIdLibro(),
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getCategoria(),
                    libro.getEditorial(),
                    libro.getAnioPublicacion(),
                    libro.getEstado()
                };
                modeloLibrosDisponibles.addRow(fila);
            }
            
            JOptionPane.showMessageDialog(this, 
                "Se encontraron " + librosEncontrados.size() + " libros que coinciden con: " + textoBusqueda, 
                "Resultados de busqueda", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al realizar la busqueda: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarPrestamos(String textoBusqueda) {
        if (textoBusqueda.isEmpty() || textoBusqueda.equals("Buscar por libro, usuario o fecha...")) {
            cargarPrestamosActivos();
            return;
        }
        
        try {
            modeloPrestamosActivos.setRowCount(0);
            
            PrestamoDAO prestamoDAO = new PrestamoDAO();
            List<Prestamo> prestamos = prestamoDAO.obtenerTodosPrestamos();
            
            List<Prestamo> prestamosFiltrados = new ArrayList<>();
            String textoLower = textoBusqueda.toLowerCase();
            
            for (Prestamo prestamo : prestamos) {
                if (prestamo.getTituloLibro().toLowerCase().contains(textoLower) ||
                    prestamo.getNombreUsuario().toLowerCase().contains(textoLower) ||
                    prestamo.getFechaPrestamo().contains(textoBusqueda) ||
                    prestamo.getFechaDevolucionEstimada().contains(textoBusqueda) ||
                    String.valueOf(prestamo.getIdPrestamo()).contains(textoBusqueda)) {
                    prestamosFiltrados.add(prestamo);
                }
            }
            
            for (Prestamo prestamo : prestamosFiltrados) {
                Object[] fila = {
                    prestamo.getIdPrestamo(),
                    prestamo.getTituloLibro(),
                    prestamo.getNombreUsuario(),
                    prestamo.getFechaPrestamo(),
                    prestamo.getFechaDevolucionEstimada(),
                    "3 Dias",
                    prestamo.getEstado()
                };
                modeloPrestamosActivos.addRow(fila);
            }
            
            if (prestamosFiltrados.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No se encontraron prestamos que coincidan con: " + textoBusqueda, 
                    "Busqueda sin resultados", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al realizar la busqueda: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}