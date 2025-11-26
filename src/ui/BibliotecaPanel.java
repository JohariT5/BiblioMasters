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
        
        // Verificar permisos
        if (!AuthService.estaLogueado() || !AuthService.esBibliotecario()) {
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
        String[] opcionesNav = {"Préstamos", "Nuevo Préstamo", "Devoluciones", "Inventario", "Salir"};
        for (String opcion : opcionesNav) {
            JButton btnNav = crearBotonNavegacion(opcion);
            navPanel.add(btnNav);
            navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        // Panel de contenido principal - PRÉSTAMOS ACTIVOS (página inicial)
        JPanel contentPanel = crearPanelPrestamosActivos();
        
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
                        case "Préstamos":
                            splitPane.setRightComponent(crearPanelPrestamosActivos());
                            break;
                        case "Nuevo Préstamo":
                            splitPane.setRightComponent(crearPanelNuevoPrestamo());
                            break;
                        case "Devoluciones":
                            splitPane.setRightComponent(crearPanelDevoluciones());
                            break;
                        case "Inventario":
                            splitPane.setRightComponent(crearPanelLibrosDisponibles());
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
    
    private JPanel crearPanelPrestamosActivos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título de sección
        JLabel lblSeccionTitulo = new JLabel("Préstamos Activos");
        lblSeccionTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblSeccionTitulo.setForeground(ThemeService.COLOR_OSCURO);
        lblSeccionTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Panel de búsqueda
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
        
        // Panel de acciones
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JButton btnActualizar = crearBotonAccion("Actualizar");
        JButton btnVerDetalles = crearBotonAccion("Ver Detalles");
        
        actionsPanel.add(btnActualizar);
        actionsPanel.add(btnVerDetalles);
        
        // Tabla de préstamos activos modernizada
        String[] columnas = {"ID", "Libro", "Usuario", "Fecha Préstamo", "Fecha Devolución", "Días Restantes", "Estado"};
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
        
        // Agregar componentes al panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        panel.add(lblSeccionTitulo, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Configurar acciones
        btnActualizar.addActionListener(e -> cargarPrestamosActivos());
        btnVerDetalles.addActionListener(e -> verDetallesPrestamo());
        
        return panel;
    }
    
    private JPanel crearPanelNuevoPrestamo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Título
        JLabel lblTitulo = new JLabel("Registrar Nuevo Préstamo");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(ThemeService.COLOR_OSCURO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Panel de búsqueda de libro
        JPanel libroPanel = new JPanel(new BorderLayout(10, 5));
        libroPanel.setBackground(Color.WHITE);
        libroPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel lblLibro = new JLabel("Buscar Libro (ISBN, Título o QR):");
        lblLibro.setFont(ThemeService.fuenteSubtitulo());
        lblLibro.setForeground(ThemeService.COLOR_OSCURO);
        
        JTextField txtBuscarLibro = new JTextField();
        txtBuscarLibro.setFont(ThemeService.fuenteNormal());
        txtBuscarLibro.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        libroPanel.add(lblLibro, BorderLayout.NORTH);
        libroPanel.add(txtBuscarLibro, BorderLayout.CENTER);
        
        // Panel de búsqueda de usuario
        JPanel usuarioPanel = new JPanel(new BorderLayout(10, 5));
        usuarioPanel.setBackground(Color.WHITE);
        usuarioPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel lblUsuario = new JLabel("Buscar Usuario (ID, Nombre o QR):");
        lblUsuario.setFont(ThemeService.fuenteSubtitulo());
        lblUsuario.setForeground(ThemeService.COLOR_OSCURO);
        
        JTextField txtBuscarUsuario = new JTextField();
        txtBuscarUsuario.setFont(ThemeService.fuenteNormal());
        txtBuscarUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        usuarioPanel.add(lblUsuario, BorderLayout.NORTH);
        usuarioPanel.add(txtBuscarUsuario, BorderLayout.CENTER);
        
        // Panel de fechas
        JPanel fechaPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        fechaPanel.setBackground(Color.WHITE);
        fechaPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel lblFechaPrestamo = new JLabel("Fecha de Préstamo:");
        lblFechaPrestamo.setFont(ThemeService.fuenteSubtitulo());
        JTextField txtFechaPrestamo = new JTextField("2024-11-09");
        txtFechaPrestamo.setFont(ThemeService.fuenteNormal());
        txtFechaPrestamo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        JLabel lblFechaDevolucion = new JLabel("Fecha de Devolución Estimada:");
        lblFechaDevolucion.setFont(ThemeService.fuenteSubtitulo());
        JTextField txtFechaDevolucion = new JTextField("2024-11-09");
        txtFechaDevolucion.setFont(ThemeService.fuenteNormal());
        txtFechaDevolucion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        fechaPanel.add(lblFechaPrestamo);
        fechaPanel.add(txtFechaPrestamo);
        fechaPanel.add(lblFechaDevolucion);
        fechaPanel.add(txtFechaDevolucion);
        
        // Botón de registrar
        JButton btnRegistrar = crearBotonAccion("Registrar Préstamo");
        btnRegistrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegistrar.setMaximumSize(new Dimension(300, 50));
        
        // Agregar componentes
        panel.add(lblTitulo);
        panel.add(libroPanel);
        panel.add(usuarioPanel);
        panel.add(fechaPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(btnRegistrar);
        
        // Configurar acción
        btnRegistrar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Función de registro de préstamo - Próximamente");
        });
        
        return panel;
    }
    
    private JPanel crearPanelDevoluciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Título
        JLabel lblTitulo = new JLabel("Devoluciones");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(ThemeService.COLOR_OSCURO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Panel de escaneo
        JPanel scanPanel = new JPanel(new BorderLayout(10, 5));
        scanPanel.setBackground(Color.WHITE);
        scanPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel lblScan = new JLabel("Escanear o ingresar código/ISBN del libro:");
        lblScan.setFont(ThemeService.fuenteSubtitulo());
        lblScan.setForeground(ThemeService.COLOR_OSCURO);
        
        JTextField txtCodigo = new JTextField();
        txtCodigo.setFont(ThemeService.fuenteNormal());
        txtCodigo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        scanPanel.add(lblScan, BorderLayout.NORTH);
        scanPanel.add(txtCodigo, BorderLayout.CENTER);
        
        // Panel de detalles del préstamo (simulado)
        JPanel detallesPanel = new JPanel();
        detallesPanel.setLayout(new BoxLayout(detallesPanel, BoxLayout.Y_AXIS));
        detallesPanel.setBackground(new Color(240, 245, 250));
        detallesPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel lblDetalles = new JLabel("Detalles del Préstamo");
        lblDetalles.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDetalles.setForeground(ThemeService.COLOR_OSCURO);
        
        JLabel lblLibro = new JLabel("Título del Libro: Cien Años de Soledad");
        JLabel lblUsuario = new JLabel("Usuario: Juan Pérez");
        JLabel lblFechaPrestamo = new JLabel("Fecha de Préstamo: 2024-10-15");
        JLabel lblAtraso = new JLabel("7 Días de Atraso");
        lblAtraso.setForeground(ThemeService.COLOR_PELIGRO);
        lblAtraso.setFont(ThemeService.fuenteSubtitulo());
        
        JLabel lblMulta = new JLabel("Multa Calculada: $7.00");
        lblMulta.setFont(ThemeService.fuenteSubtitulo());
        
        detallesPanel.add(lblDetalles);
        detallesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detallesPanel.add(lblLibro);
        detallesPanel.add(lblUsuario);
        detallesPanel.add(lblFechaPrestamo);
        detallesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detallesPanel.add(lblAtraso);
        detallesPanel.add(lblMulta);
        
        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton btnPerdonarMulta = crearBotonAccion("Perdonar Multa");
        JButton btnConfirmar = crearBotonAccion("Confirmar Devolución");
        
        buttonPanel.add(btnPerdonarMulta);
        buttonPanel.add(btnConfirmar);
        
        // Agregar componentes
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
        
        // Configurar acciones
        btnConfirmar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Devolución registrada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        });
        
        btnPerdonarMulta.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Multa perdonada", "Información", JOptionPane.INFORMATION_MESSAGE);
        });
        
        return panel;
    }
    
    private JPanel crearPanelLibrosDisponibles() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título de sección
        JLabel lblSeccionTitulo = new JLabel("Inventario - Libros Disponibles");
        lblSeccionTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblSeccionTitulo.setForeground(ThemeService.COLOR_OSCURO);
        lblSeccionTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JTextField txtBusqueda = new JTextField();
        txtBusqueda.setFont(ThemeService.fuenteNormal());
        txtBusqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        txtBusqueda.setText("Buscar por título, autor, ISBN o QR...");
        
        JButton btnBuscar = new JButton("🔍 Buscar");
        btnBuscar.setFont(ThemeService.fuenteNormal());
        btnBuscar.setBackground(ThemeService.COLOR_PRIMARIO);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnBuscar.setFocusPainted(false);
        
        searchPanel.add(txtBusqueda, BorderLayout.CENTER);
        searchPanel.add(btnBuscar, BorderLayout.EAST);
        
        // Tabla de libros disponibles modernizada
        String[] columnas = {"ID", "Título", "Autor", "Ejemplares (Total)", "Ejemplares (Disponibles)", "Estado"};
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
        
        // Agregar componentes al panel
        panel.add(lblSeccionTitulo, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        // Configurar acciones
        btnBuscar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Búsqueda en desarrollo", "Información", JOptionPane.INFORMATION_MESSAGE);
        });
        
        return panel;
    }
    
    // MÉTODOS ORIGINALES PRESERVADOS
    
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
        cargarPrestamosActivos();
        cargarLibrosDisponibles();
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
                    "3 Días", // Placeholder para días restantes
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
                if ("DISPONIBLE".equals(libro.getEstado())) {
                    Object[] fila = {
                        libro.getIdLibro(),
                        libro.getTitulo(),
                        libro.getAutor(),
                        "1", // Ejemplares totales - placeholder
                        "1", // Ejemplares disponibles - placeholder
                        libro.getEstado()
                    };
                    modeloLibrosDisponibles.addRow(fila);
                }
            }
        }
    }
    
    private void verDetallesPrestamo() {
        if (tablaPrestamosActivos.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un préstamo", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Detalles del préstamo - Próximamente");
    }
}