package ui;

import service.AuthService;
import service.ThemeService;
import dao.LibroDAO;
import dao.PrestamoDAO;
import model.Libro;
import model.Prestamo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsuarioPanel extends JFrame {
    private JTable tablalibrosDisponibles;
    private DefaultTableModel modelolibrosDisponibles;
    private JTable tablaMiHistorial;
    private DefaultTableModel modeloMiHistorial;
    private JTextField txtBusqueda;
    
    public UsuarioPanel() {
        setTitle("BiblioMasters - Panel de Usuario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Verificar permisos
        if (!AuthService.estaLogueado() || (!AuthService.esEstudiante() && !AuthService.esDocente())) {
            JOptionPane.showMessageDialog(this, "Acceso denegado", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        // Panel principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Header moderno similar a las fotos
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeService.COLOR_PRIMARIO);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        headerPanel.setPreferredSize(new Dimension(1200, 80));
        
        // Título a la izquierda
        JLabel lblTitulo = new JLabel("BiblioMasters");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        
        // Panel de usuario a la derecha
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setBackground(ThemeService.COLOR_PRIMARIO);
        
        JLabel lblUsuario = new JLabel(AuthService.getUsuarioLogueado().getNombre() + " (" + AuthService.getUsuarioLogueado().getTipoUsuario() + ")");
        lblUsuario.setFont(ThemeService.fuenteNormal());
        lblUsuario.setForeground(Color.WHITE);
        
        JButton btnLogout = crearBotonHeader("Salir", ThemeService.COLOR_PRIMARIO);
        JButton btnCambiarUsuario = crearBotonHeader("Cambiar", ThemeService.COLOR_PRIMARIO);
        
        userPanel.add(lblUsuario);
        userPanel.add(btnCambiarUsuario);
        userPanel.add(btnLogout);
        
        headerPanel.add(lblTitulo, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        // Panel de navegación lateral (como en las fotos)
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeService.COLOR_CLARO),
            BorderFactory.createEmptyBorder(20, 0, 20, 0)
        ));
        navPanel.setPreferredSize(new Dimension(200, 0));
        
        // Botones de navegación
        String[] opcionesNav = {"Libros Disponibles", "Mi Historial", "Mi Información", "Salir"};
        for (String opcion : opcionesNav) {
            JButton btnNav = crearBotonNavegacion(opcion);
            navPanel.add(btnNav);
            navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        // Panel de contenido principal - LIBROS DISPONIBLES (página inicial)
        JPanel contentPanel = crearPanelLibrosDisponibles();
        
        // Panel principal con split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, navPanel, contentPanel);
        splitPane.setDividerLocation(200);
        splitPane.setDividerSize(2);
        splitPane.setEnabled(false);
        
        // Agregar todo al panel principal
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Configurar acciones
        btnLogout.addActionListener(e -> logout());
        btnCambiarUsuario.addActionListener(e -> cambiarUsuario());
        
        // Configurar navegación
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
                        case "Libros Disponibles":
                            splitPane.setRightComponent(crearPanelLibrosDisponibles());
                            break;
                        case "Mi Historial":
                            splitPane.setRightComponent(crearPanelMiHistorial());
                            break;
                        case "Mi Información":
                            splitPane.setRightComponent(crearPanelMiInfo());
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
        
        // Efecto hover - azul claro al pasar el mouse
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
    
    // PANELES MODERNIZADOS
    
    private JPanel crearPanelLibrosDisponibles() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título de sección
        JLabel lblSeccionTitulo = new JLabel("Libros Disponibles");
        lblSeccionTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblSeccionTitulo.setForeground(ThemeService.COLOR_OSCURO);
        lblSeccionTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        txtBusqueda = new JTextField();
        txtBusqueda.setFont(ThemeService.fuenteNormal());
        txtBusqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        txtBusqueda.setText("Buscar por título, autor, ISBN o QR...");
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(ThemeService.fuenteNormal());
        btnBuscar.setBackground(ThemeService.COLOR_PRIMARIO);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnBuscar.setFocusPainted(false);
        
        JButton btnLimpiar = new JButton("🗑️ Limpiar");
        btnLimpiar.setFont(ThemeService.fuenteNormal());
        btnLimpiar.setBackground(ThemeService.COLOR_SECUNDARIO);
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLimpiar.setFocusPainted(false);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnBuscar);
        buttonPanel.add(btnLimpiar);
        
        searchPanel.add(txtBusqueda, BorderLayout.CENTER);
        searchPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Panel de acciones
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JButton btnActualizar = crearBotonAccion("Actualizar");
        JButton btnVerDetalles = crearBotonAccion("Ver Detalles");
        
        actionsPanel.add(btnActualizar);
        actionsPanel.add(btnVerDetalles);
        
        // Tabla de libros disponibles modernizada
        String[] columnas = {"ID", "Título", "Autor", "Categoría", "Editorial", "Año", "Estado"};
        modelolibrosDisponibles = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablalibrosDisponibles = new JTable(modelolibrosDisponibles);
        tablalibrosDisponibles.setFont(ThemeService.fuenteNormal());
        tablalibrosDisponibles.setRowHeight(35);
        tablalibrosDisponibles.setSelectionBackground(new Color(220, 240, 255));
        tablalibrosDisponibles.setSelectionForeground(ThemeService.COLOR_OSCURO);
        tablalibrosDisponibles.setGridColor(ThemeService.COLOR_CLARO);
        
        JScrollPane scrollPane = new JScrollPane(tablalibrosDisponibles);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Agregar componentes al panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        panel.add(lblSeccionTitulo, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Configurar acciones
        btnActualizar.addActionListener(e -> cargarLibrosDisponibles());
        btnBuscar.addActionListener(e -> buscarLibros());
        btnLimpiar.addActionListener(e -> {
            txtBusqueda.setText("");
            cargarLibrosDisponibles();
        });
        btnVerDetalles.addActionListener(e -> verDetallesLibro());
        
        // Buscar al presionar Enter
        txtBusqueda.addActionListener(e -> buscarLibros());
        
        return panel;
    }
    
    private JPanel crearPanelMiHistorial() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título de sección
        JLabel lblSeccionTitulo = new JLabel("Mi Historial de Préstamos");
        lblSeccionTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblSeccionTitulo.setForeground(ThemeService.COLOR_OSCURO);
        lblSeccionTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Panel de acciones
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JButton btnActualizar = crearBotonAccion("Actualizar Historial");
        actionsPanel.add(btnActualizar);
        
        // Tabla de historial modernizada
        String[] columnas = {"ID", "Libro", "Fecha Préstamo", "Fecha Devolución Estimada", "Fecha Devolución Real", "Estado"};
        modeloMiHistorial = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaMiHistorial = new JTable(modeloMiHistorial);
        tablaMiHistorial.setFont(ThemeService.fuenteNormal());
        tablaMiHistorial.setRowHeight(35);
        tablaMiHistorial.setSelectionBackground(new Color(220, 240, 255));
        tablaMiHistorial.setSelectionForeground(ThemeService.COLOR_OSCURO);
        tablaMiHistorial.setGridColor(ThemeService.COLOR_CLARO);
        
        JScrollPane scrollPane = new JScrollPane(tablaMiHistorial);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Agregar componentes al panel
        panel.add(lblSeccionTitulo, BorderLayout.NORTH);
        panel.add(actionsPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        btnActualizar.addActionListener(e -> cargarMiHistorial());
        
        return panel;
    }
    
    private JPanel crearPanelMiInfo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Título
        JLabel lblTitulo = new JLabel("Mi Información");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(ThemeService.COLOR_OSCURO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Panel de información
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(240, 245, 250));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        
        // Información del usuario
        JLabel lblNombre = crearEtiquetaInfo("Nombre: " + AuthService.getUsuarioLogueado().getNombre());
        JLabel lblEmail = crearEtiquetaInfo("Email: " + AuthService.getUsuarioLogueado().getEmail());
        JLabel lblTipo = crearEtiquetaInfo("Tipo: " + AuthService.getUsuarioLogueado().getTipoUsuario());
        JLabel lblCodigo = crearEtiquetaInfo("Código: " + AuthService.getUsuarioLogueado().getCodigoIdentificacion());
        
        // Separador
        JSeparator separator = new JSeparator();
        separator.setForeground(ThemeService.COLOR_CLARO);
        separator.setMaximumSize(new Dimension(500, 2));
        
        // Funciones disponibles
        JLabel lblFunciones = new JLabel("Funciones Disponibles:");
        lblFunciones.setFont(ThemeService.fuenteSubtitulo());
        lblFunciones.setForeground(ThemeService.COLOR_OSCURO);
        lblFunciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblFunc1 = crearEtiquetaInfo("• Consultar libros disponibles");
        JLabel lblFunc2 = crearEtiquetaInfo("• Buscar libros por título/autor");
        JLabel lblFunc3 = crearEtiquetaInfo("• Ver historial de préstamos");
        JLabel lblFunc4 = crearEtiquetaInfo("• Consultar información personal");
        
        JLabel lblNota = new JLabel("<html><div style='text-align: center; margin-top: 20px; color: #666;'>Para solicitar préstamos, contacta al bibliotecario.</div></html>");
        lblNota.setFont(ThemeService.fuentePequeña());
        lblNota.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Agregar componentes al panel de información
        infoPanel.add(lblNombre);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(lblEmail);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(lblTipo);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(lblCodigo);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(separator);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(lblFunciones);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(lblFunc1);
        infoPanel.add(lblFunc2);
        infoPanel.add(lblFunc3);
        infoPanel.add(lblFunc4);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(lblNota);
        
        // Centrar el panel de información
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(infoPanel);
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JLabel crearEtiquetaInfo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(ThemeService.fuenteNormal());
        label.setForeground(ThemeService.COLOR_OSCURO);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    // MÉTODOS ORIGINALES PRESERVADOS - SIN CAMBIOS EN LA LÓGICA
    
    private void logout() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Estás seguro de que quieres cerrar sesión?",
            "Confirmar Cierre de Sesión",
            JOptionPane.YES_NO_OPTION);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            AuthService.logout();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    private void cambiarUsuario() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Quieres cambiar de usuario? Se cerrará la sesión actual.",
            "Cambiar Usuario",
            JOptionPane.YES_NO_OPTION);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            AuthService.logout();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    private void cargarDatos() {
        cargarLibrosDisponibles();
        cargarMiHistorial();
    }
    
    private void cargarLibrosDisponibles() {
        if (modelolibrosDisponibles != null) {
            modelolibrosDisponibles.setRowCount(0);
            
            LibroDAO libroDAO = new LibroDAO();
            List<Libro> libros = libroDAO.obtenerTodosLibros();
            
            for (Libro libro : libros) {
                if ("DISPONIBLE".equals(libro.getEstado())) {
                    Object[] fila = {
                        libro.getIdLibro(),
                        libro.getTitulo(),
                        libro.getAutor(),
                        libro.getCategoria(),
                        libro.getEditorial(),
                        libro.getAnioPublicacion(),
                        libro.getEstado()
                    };
                    modelolibrosDisponibles.addRow(fila);
                }
            }
        }
    }
    
    private void cargarMiHistorial() {
        if (modeloMiHistorial != null) {
            modeloMiHistorial.setRowCount(0);
            
            PrestamoDAO prestamoDAO = new PrestamoDAO();
            List<Prestamo> todosPrestamos = prestamoDAO.obtenerTodosPrestamos();
            int usuarioId = AuthService.getUsuarioLogueado().getIdUsuario();
            
            for (Prestamo prestamo : todosPrestamos) {
                if (prestamo.getIdUsuario() == usuarioId) {
                    Object[] fila = {
                        prestamo.getIdPrestamo(),
                        prestamo.getTituloLibro(),
                        prestamo.getFechaPrestamo(),
                        prestamo.getFechaDevolucionEstimada(),
                        prestamo.getFechaDevolucionReal() != null ? prestamo.getFechaDevolucionReal() : "Pendiente",
                        prestamo.getEstado()
                    };
                    modeloMiHistorial.addRow(fila);
                }
            }
        }
    }
    
    private void buscarLibros() {
        String busqueda = txtBusqueda.getText().trim().toLowerCase();
        
        if (busqueda.isEmpty()) {
            cargarLibrosDisponibles();
            return;
        }
        
        modelolibrosDisponibles.setRowCount(0);
        
        LibroDAO libroDAO = new LibroDAO();
        List<Libro> libros = libroDAO.obtenerTodosLibros();
        
        for (Libro libro : libros) {
            if ("DISPONIBLE".equals(libro.getEstado())) {
                boolean coincide = libro.getTitulo().toLowerCase().contains(busqueda) ||
                                 libro.getAutor().toLowerCase().contains(busqueda) ||
                                 libro.getCategoria().toLowerCase().contains(busqueda) ||
                                 libro.getEditorial().toLowerCase().contains(busqueda);
                
                if (coincide) {
                    Object[] fila = {
                        libro.getIdLibro(),
                        libro.getTitulo(),
                        libro.getAutor(),
                        libro.getCategoria(),
                        libro.getEditorial(),
                        libro.getAnioPublicacion(),
                        libro.getEstado()
                    };
                    modelolibrosDisponibles.addRow(fila);
                }
            }
        }
        
        if (modelolibrosDisponibles.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No se encontraron libros con: " + busqueda, "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void verDetallesLibro() {
        int filaSeleccionada = tablalibrosDisponibles.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un libro para ver detalles", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idLibro = (int) modelolibrosDisponibles.getValueAt(filaSeleccionada, 0);
        String titulo = (String) modelolibrosDisponibles.getValueAt(filaSeleccionada, 1);
        String autor = (String) modelolibrosDisponibles.getValueAt(filaSeleccionada, 2);
        String categoria = (String) modelolibrosDisponibles.getValueAt(filaSeleccionada, 3);
        String editorial = (String) modelolibrosDisponibles.getValueAt(filaSeleccionada, 4);
        int año = (int) modelolibrosDisponibles.getValueAt(filaSeleccionada, 5);
        
        String detalles = "Detalles del Libro\n\n" +
                         "Título: " + titulo + "\n" +
                         "Autor: " + autor + "\n" +
                         "Categoría: " + categoria + "\n" +
                         "Editorial: " + editorial + "\n" +
                         "Año: " + año + "\n" +
                         "ID: " + idLibro + "\n" +
                         "Estado: DISPONIBLE\n\n" +
                         "Para solicitar este libro, contacta al bibliotecario.";
        
        JOptionPane.showMessageDialog(this, detalles, "Detalles del Libro", JOptionPane.INFORMATION_MESSAGE);
    }
}