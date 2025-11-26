package ui;

import service.AuthService;
import service.ThemeService;
import service.QRService;
import dao.LibroDAO;
import dao.UsuarioDAO;
import dao.PrestamoDAO;
import model.Libro;
import model.Usuario;
import model.Prestamo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AdminPanel extends JFrame {
    private JTable tablaLibros;
    private DefaultTableModel modeloTablaLibros;
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTablaUsuarios;
    private JTable tablaPrestamos;
    private DefaultTableModel modeloTablaPrestamos;
    
    public AdminPanel() {
        setTitle("BiblioMasters - Panel de Administración");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Verificar permisos
        if (!AuthService.estaLogueado() || !AuthService.esAdministrador()) {
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
        
        JLabel lblUsuario = new JLabel( AuthService.getUsuarioLogueado().getNombre() + " (Admin)");
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
        String[] opcionesNav = {"Inventario", "Usuarios", "Préstamos", "Reportes", "Salir"};
        for (String opcion : opcionesNav) {
            JButton btnNav = crearBotonNavegacion(opcion);
            navPanel.add(btnNav);
            navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        // Panel de contenido principal - INVENTARIO (página inicial)
        JPanel contentPanel = crearPanelInventario();
        
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
                        case "Inventario":
                            splitPane.setRightComponent(crearPanelInventario());
                            break;
                        case "Usuarios":
                            splitPane.setRightComponent(crearPanelUsuarios());
                            break;
                        case "Préstamos":
                            splitPane.setRightComponent(crearPanelPrestamos());
                            break;
                        case "Reportes":
                            splitPane.setRightComponent(crearPanelReportes());
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
    
    // PANELES COMPLETOS
    
    private JPanel crearPanelInventario() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título de sección
        JLabel lblSeccionTitulo = new JLabel("Gestión de Inventario");
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
        
        JButton btnAgregar = crearBotonAccion("Agregar Libro");
        JButton btnEditar = crearBotonAccion("Editar");
        JButton btnEliminar = crearBotonAccion("Eliminar");
        JButton btnQR = crearBotonAccion("📱 QR");
        JButton btnActualizar = crearBotonAccion("Actualizar");
        
        actionsPanel.add(btnAgregar);
        actionsPanel.add(btnEditar);
        actionsPanel.add(btnEliminar);
        actionsPanel.add(btnQR);
        actionsPanel.add(btnActualizar);
        
        // Tabla de libros modernizada
        String[] columnas = {"ID", "Título", "Autor", "Categoría", "Editorial", "Año", "Estado", "QR"};
        modeloTablaLibros = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaLibros = new JTable(modeloTablaLibros);
        tablaLibros.setFont(ThemeService.fuenteNormal());
        tablaLibros.setRowHeight(35);
        tablaLibros.setSelectionBackground(new Color(220, 240, 255));
        tablaLibros.setSelectionForeground(ThemeService.COLOR_OSCURO);
        tablaLibros.setGridColor(ThemeService.COLOR_CLARO);
        
        JScrollPane scrollPane = new JScrollPane(tablaLibros);
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
        btnAgregar.addActionListener(e -> agregarLibro());
        btnActualizar.addActionListener(e -> cargarLibros());
        btnEditar.addActionListener(e -> editarLibro());
        btnEliminar.addActionListener(e -> eliminarLibro());
        btnQR.addActionListener(e -> generarQR());
        
        return panel;
    }
    
    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título de sección
        JLabel lblSeccionTitulo = new JLabel("Gestión de Usuarios");
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
        txtBusqueda.setText("Buscar por nombre, email o código...");
        
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
        
        JButton btnAgregar = crearBotonAccion("Agregar Usuario");
        JButton btnEditar = crearBotonAccion("Editar");
        JButton btnEliminar = crearBotonAccion("Eliminar");
        JButton btnActualizar = crearBotonAccion("Actualizar");
        
        actionsPanel.add(btnAgregar);
        actionsPanel.add(btnEditar);
        actionsPanel.add(btnEliminar);
        actionsPanel.add(btnActualizar);
        
        // Tabla de usuarios
        String[] columnas = {"ID", "Nombre", "Email", "Tipo", "Código", "Fecha Registro"};
        modeloTablaUsuarios = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaUsuarios = new JTable(modeloTablaUsuarios);
        tablaUsuarios.setFont(ThemeService.fuenteNormal());
        tablaUsuarios.setRowHeight(35);
        tablaUsuarios.setSelectionBackground(new Color(220, 240, 255));
        tablaUsuarios.setSelectionForeground(ThemeService.COLOR_OSCURO);
        tablaUsuarios.setGridColor(ThemeService.COLOR_CLARO);
        
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
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
        btnAgregar.addActionListener(e -> agregarUsuario());
        btnActualizar.addActionListener(e -> cargarUsuarios());
        btnEditar.addActionListener(e -> editarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        
        return panel;
    }
    
    private JPanel crearPanelPrestamos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título de sección
        JLabel lblSeccionTitulo = new JLabel("Gestión de Préstamos");
        lblSeccionTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblSeccionTitulo.setForeground(ThemeService.COLOR_OSCURO);
        lblSeccionTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Panel de acciones
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JButton btnActualizar = crearBotonAccion("Actualizar");
        JButton btnVerDetalles = crearBotonAccion("Ver Detalles");
        JButton btnRegistrarDevolucion = crearBotonAccion("Registrar Devolución");
        JButton btnExportar = crearBotonAccion("Exportar Reporte");
        
        actionsPanel.add(btnActualizar);
        actionsPanel.add(btnVerDetalles);
        actionsPanel.add(btnRegistrarDevolucion);
        actionsPanel.add(btnExportar);
        
        // Tabla de préstamos
        String[] columnas = {"ID", "Libro", "Usuario", "Fecha Préstamo", "Fecha Devolución", "Estado", "Días Restantes"};
        modeloTablaPrestamos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaPrestamos = new JTable(modeloTablaPrestamos);
        tablaPrestamos.setFont(ThemeService.fuenteNormal());
        tablaPrestamos.setRowHeight(35);
        tablaPrestamos.setSelectionBackground(new Color(220, 240, 255));
        tablaPrestamos.setSelectionForeground(ThemeService.COLOR_OSCURO);
        tablaPrestamos.setGridColor(ThemeService.COLOR_CLARO);
        
        JScrollPane scrollPane = new JScrollPane(tablaPrestamos);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Agregar componentes al panel
        panel.add(lblSeccionTitulo, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
        // Configurar acciones
        btnActualizar.addActionListener(e -> cargarPrestamos());
        btnVerDetalles.addActionListener(e -> verDetallesPrestamo());
        btnRegistrarDevolucion.addActionListener(e -> registrarDevolucion());
        btnExportar.addActionListener(e -> exportarReportePrestamos());
        
        return panel;
    }
    
    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Título
        JLabel lblTitulo = new JLabel("Reportes y Estadísticas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(ThemeService.COLOR_OSCURO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Panel de estadísticas
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        
        // Obtener datos reales
        LibroDAO libroDAO = new LibroDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        PrestamoDAO prestamoDAO = new PrestamoDAO();
        
        List<Libro> libros = libroDAO.obtenerTodosLibros();
        List<Usuario> usuarios = usuarioDAO.obtenerTodosUsuarios();
        List<Prestamo> prestamosActivos = prestamoDAO.obtenerPrestamosActivos();
        List<Prestamo> todosPrestamos = prestamoDAO.obtenerTodosPrestamos();
        
        long prestamosVencidos = todosPrestamos.stream()
            .filter(p -> "VENCIDO".equals(p.getEstado()))
            .count();
        
        // Tarjetas de estadísticas con datos reales
        JPanel cardLibros = crearTarjetaEstadistica("Total Libros", String.valueOf(libros.size()), ThemeService.COLOR_PRIMARIO);
        JPanel cardUsuarios = crearTarjetaEstadistica("Total Usuarios", String.valueOf(usuarios.size()), ThemeService.COLOR_EXITO);
        JPanel cardPrestamos = crearTarjetaEstadistica("Préstamos Activos", String.valueOf(prestamosActivos.size()), ThemeService.COLOR_ADVERTENCIA);
        JPanel cardDevoluciones = crearTarjetaEstadistica("Préstamos Vencidos", String.valueOf(prestamosVencidos), ThemeService.COLOR_PELIGRO);
        
        statsPanel.add(cardLibros);
        statsPanel.add(cardUsuarios);
        statsPanel.add(cardPrestamos);
        statsPanel.add(cardDevoluciones);
        
        // Panel de acciones de reportes
        JPanel reportActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        reportActionsPanel.setBackground(Color.WHITE);
        reportActionsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        JButton btnReporteLibros = crearBotonAccion("Reporte de Libros");
        JButton btnReporteUsuarios = crearBotonAccion("Reporte de Usuarios");
        JButton btnReportePrestamos = crearBotonAccion("Reporte de Préstamos");
        
        reportActionsPanel.add(btnReporteLibros);
        reportActionsPanel.add(btnReporteUsuarios);
        reportActionsPanel.add(btnReportePrestamos);
        
        // Agregar componentes
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        panel.add(reportActionsPanel, BorderLayout.SOUTH);
        
        // Configurar acciones
        btnReporteLibros.addActionListener(e -> generarReporteLibros());
        btnReporteUsuarios.addActionListener(e -> generarReporteUsuarios());
        btnReportePrestamos.addActionListener(e -> generarReportePrestamos());
        
        return panel;
    }
    
    private JPanel crearTarjetaEstadistica(String titulo, String valor, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeService.COLOR_CLARO, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(ThemeService.fuenteNormal());
        lblTitulo.setForeground(ThemeService.COLOR_OSCURO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValor.setForeground(color);
        lblValor.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(lblTitulo);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblValor);
        
        return card;
    }
    
    // MÉTODOS PARA GESTIÓN DE LIBROS
    private void cargarDatos() {
        cargarLibros();
        cargarUsuarios();
        cargarPrestamos();
    }
    
    private void cargarLibros() {
        if (modeloTablaLibros != null) {
            modeloTablaLibros.setRowCount(0);
            
            LibroDAO libroDAO = new LibroDAO();
            List<Libro> libros = libroDAO.obtenerTodosLibros();
            
            for (Libro libro : libros) {
                String estadoQR = QRService.existeQR(libro.getIdLibro()) ? "SI" : "NO";
                
                Object[] fila = {
                    libro.getIdLibro(),
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getCategoria(),
                    libro.getEditorial(),
                    libro.getAnioPublicacion(),
                    libro.getEstado(),
                    estadoQR
                };
                modeloTablaLibros.addRow(fila);
            }
        }
    }
    
    private void agregarLibro() {
        JTextField txtTitulo = new JTextField();
        JTextField txtAutor = new JTextField();
        JTextField txtIsbn = new JTextField();
        JTextField txtCategoria = new JTextField();
        JTextField txtEditorial = new JTextField();
        JTextField txtAnio = new JTextField();
        
        Object[] message = {
            "Título:", txtTitulo,
            "Autor:", txtAutor,
            "ISBN:", txtIsbn,
            "Categoría:", txtCategoria,
            "Editorial:", txtEditorial,
            "Año Publicación:", txtAnio
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Agregar Nuevo Libro", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            try {
                Libro nuevoLibro = new Libro();
                nuevoLibro.setTitulo(txtTitulo.getText());
                nuevoLibro.setAutor(txtAutor.getText());
                nuevoLibro.setIsbn(txtIsbn.getText());
                nuevoLibro.setCategoria(txtCategoria.getText());
                nuevoLibro.setEditorial(txtEditorial.getText());
                nuevoLibro.setAnioPublicacion(Integer.parseInt(txtAnio.getText()));
                nuevoLibro.setEstado("DISPONIBLE");
                
                LibroDAO libroDAO = new LibroDAO();
                if (libroDAO.agregarLibro(nuevoLibro)) {
                    JOptionPane.showMessageDialog(this, "Libro agregado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarLibros();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar libro", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El año debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editarLibro() {
        int filaSeleccionada = tablaLibros.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un libro para editar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idLibro = (int) modeloTablaLibros.getValueAt(filaSeleccionada, 0);
        
        LibroDAO libroDAO = new LibroDAO();
        Libro libro = libroDAO.buscarPorId(idLibro);
        
        if (libro == null) {
            JOptionPane.showMessageDialog(this, "Libro no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JTextField txtTitulo = new JTextField(libro.getTitulo());
        JTextField txtAutor = new JTextField(libro.getAutor());
        JTextField txtIsbn = new JTextField(libro.getIsbn());
        JTextField txtCategoria = new JTextField(libro.getCategoria());
        JTextField txtEditorial = new JTextField(libro.getEditorial());
        JTextField txtAnio = new JTextField(String.valueOf(libro.getAnioPublicacion()));
        
        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"DISPONIBLE", "PRESTADO", "MANTENIMIENTO"});
        cmbEstado.setSelectedItem(libro.getEstado());
        
        Object[] message = {
            "Título:", txtTitulo,
            "Autor:", txtAutor,
            "ISBN:", txtIsbn,
            "Categoría:", txtCategoria,
            "Editorial:", txtEditorial,
            "Año Publicación:", txtAnio,
            "Estado:", cmbEstado
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Editar Libro", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            try {
                libro.setTitulo(txtTitulo.getText());
                libro.setAutor(txtAutor.getText());
                libro.setIsbn(txtIsbn.getText());
                libro.setCategoria(txtCategoria.getText());
                libro.setEditorial(txtEditorial.getText());
                libro.setAnioPublicacion(Integer.parseInt(txtAnio.getText()));
                libro.setEstado((String) cmbEstado.getSelectedItem());
                
                if (libroDAO.actualizarLibro(libro)) {
                    JOptionPane.showMessageDialog(this, "Libro actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarLibros();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar libro", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El año debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void eliminarLibro() {
        int filaSeleccionada = tablaLibros.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un libro para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idLibro = (int) modeloTablaLibros.getValueAt(filaSeleccionada, 0);
        String titulo = (String) modeloTablaLibros.getValueAt(filaSeleccionada, 1);
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Estás seguro de eliminar el libro:\n\"" + titulo + "\"?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            LibroDAO libroDAO = new LibroDAO();
            if (libroDAO.eliminarLibro(idLibro)) {
                JOptionPane.showMessageDialog(this, "Libro eliminado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarLibros();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar libro", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void generarQR() {
        int filaSeleccionada = tablaLibros.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un libro para generar QR", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idLibro = (int) modeloTablaLibros.getValueAt(filaSeleccionada, 0);
        String titulo = (String) modeloTablaLibros.getValueAt(filaSeleccionada, 1);
        String autor = (String) modeloTablaLibros.getValueAt(filaSeleccionada, 2);
        
        // Generar QR
        String rutaQR = QRService.generarQRParaLibro(idLibro, titulo, autor);
        
        if (rutaQR != null) {
            JOptionPane.showMessageDialog(this, 
                "✅ Código QR generado exitosamente!\n\n" +
                "Libro: " + titulo + "\n" +
                "Archivo: " + rutaQR,
                "QR Generado", 
                JOptionPane.INFORMATION_MESSAGE);
            
            cargarLibros(); // Actualizar tabla para mostrar "SI" en columna QR
        } else {
            JOptionPane.showMessageDialog(this, "Error al generar código QR", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // MÉTODOS PARA GESTIÓN DE USUARIOS
    private void cargarUsuarios() {
        if (modeloTablaUsuarios != null) {
            modeloTablaUsuarios.setRowCount(0);
            
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            List<Usuario> usuarios = usuarioDAO.obtenerTodosUsuarios();
            
            for (Usuario usuario : usuarios) {
                Object[] fila = {
                    usuario.getIdUsuario(),
                    usuario.getNombre(),
                    usuario.getEmail(),
                    usuario.getTipoUsuario(),
                    usuario.getCodigoIdentificacion(),
                    usuario.getFechaRegistro()
                };
                modeloTablaUsuarios.addRow(fila);
            }
        }
    }
    
    private void agregarUsuario() {
        JTextField txtNombre = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtPassword = new JTextField();
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"ESTUDIANTE", "DOCENTE", "BIBLIOTECARIO", "ADMINISTRADOR"});
        JTextField txtCodigo = new JTextField();
        
        Object[] message = {
            "Nombre:", txtNombre,
            "Email:", txtEmail,
            "Contraseña:", txtPassword,
            "Tipo de Usuario:", cmbTipo,
            "Código Identificación:", txtCodigo
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Agregar Nuevo Usuario", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            if (txtNombre.getText().isEmpty() || txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor completa todos los campos obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(txtNombre.getText());
            nuevoUsuario.setEmail(txtEmail.getText());
            nuevoUsuario.setPassword(txtPassword.getText());
            nuevoUsuario.setTipoUsuario((String) cmbTipo.getSelectedItem());
            nuevoUsuario.setCodigoIdentificacion(txtCodigo.getText());
            
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            if (usuarioDAO.agregarUsuario(nuevoUsuario)) {
                JOptionPane.showMessageDialog(this, "Usuario agregado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuarios();
            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar usuario", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editarUsuario() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un usuario para editar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idUsuario = (int) modeloTablaUsuarios.getValueAt(filaSeleccionada, 0);
        
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        
        if (usuario == null) {
            JOptionPane.showMessageDialog(this, "Usuario no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JTextField txtNombre = new JTextField(usuario.getNombre());
        JTextField txtEmail = new JTextField(usuario.getEmail());
        JTextField txtPassword = new JTextField(usuario.getPassword());
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"ESTUDIANTE", "DOCENTE", "BIBLIOTECARIO", "ADMINISTRADOR"});
        cmbTipo.setSelectedItem(usuario.getTipoUsuario());
        JTextField txtCodigo = new JTextField(usuario.getCodigoIdentificacion());
        
        Object[] message = {
            "Nombre:", txtNombre,
            "Email:", txtEmail,
            "Contraseña:", txtPassword,
            "Tipo de Usuario:", cmbTipo,
            "Código Identificación:", txtCodigo
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Editar Usuario", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            if (txtNombre.getText().isEmpty() || txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor completa todos los campos obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            usuario.setNombre(txtNombre.getText());
            usuario.setEmail(txtEmail.getText());
            usuario.setPassword(txtPassword.getText());
            usuario.setTipoUsuario((String) cmbTipo.getSelectedItem());
            usuario.setCodigoIdentificacion(txtCodigo.getText());
            
            if (usuarioDAO.actualizarUsuario(usuario)) {
                JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuarios();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar usuario", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void eliminarUsuario() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un usuario para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idUsuario = (int) modeloTablaUsuarios.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modeloTablaUsuarios.getValueAt(filaSeleccionada, 1);
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Estás seguro de eliminar al usuario:\n\"" + nombre + "\"?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            if (usuarioDAO.eliminarUsuario(idUsuario)) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuarios();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar usuario", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // MÉTODOS PARA GESTIÓN DE PRÉSTAMOS
    private void cargarPrestamos() {
        if (modeloTablaPrestamos != null) {
            modeloTablaPrestamos.setRowCount(0);
            
            PrestamoDAO prestamoDAO = new PrestamoDAO();
            List<Prestamo> prestamos = prestamoDAO.obtenerTodosPrestamos();
                        
            for (Prestamo prestamo : prestamos) {
                String diasRestantes = calcularDiasRestantes(prestamo.getFechaDevolucionEstimada());
                
                Object[] fila = {
                    prestamo.getIdPrestamo(),
                    prestamo.getTituloLibro(),
                    prestamo.getNombreUsuario(),
                    prestamo.getFechaPrestamo(),
                    prestamo.getFechaDevolucionEstimada(),
                    prestamo.getEstado(),
                    diasRestantes
                };
                modeloTablaPrestamos.addRow(fila);
            }
        }
    }
    
    private String calcularDiasRestantes(String fechaDevolucion) {
        try {
            LocalDate fechaDev = LocalDate.parse(fechaDevolucion);
            LocalDate hoy = LocalDate.now();
            
            if (hoy.isAfter(fechaDev)) {
                long diasPasados = ChronoUnit.DAYS.between(fechaDev, hoy);
                return "Vencido hace " + diasPasados + " días";
            } else {
                long diasRestantes = ChronoUnit.DAYS.between(hoy, fechaDev);
                return diasRestantes + " días";
            }
        } catch (Exception e) {
            return "N/A";
        }
    }
    
    private void verDetallesPrestamo() {
        int filaSeleccionada = tablaPrestamos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un préstamo", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idPrestamo = (int) modeloTablaPrestamos.getValueAt(filaSeleccionada, 0);
        String libro = (String) modeloTablaPrestamos.getValueAt(filaSeleccionada, 1);
        String usuario = (String) modeloTablaPrestamos.getValueAt(filaSeleccionada, 2);
        String fechaPrestamo = (String) modeloTablaPrestamos.getValueAt(filaSeleccionada, 3);
        String fechaDevolucion = (String) modeloTablaPrestamos.getValueAt(filaSeleccionada, 4);
        String estado = (String) modeloTablaPrestamos.getValueAt(filaSeleccionada, 5);
        String diasRestantes = (String) modeloTablaPrestamos.getValueAt(filaSeleccionada, 6);
        
        String mensaje = "Detalles del Préstamo:\n\n" +
                        "ID: " + idPrestamo + "\n" +
                        "Libro: " + libro + "\n" +
                        "Usuario: " + usuario + "\n" +
                        "Fecha Préstamo: " + fechaPrestamo + "\n" +
                        "Fecha Devolución: " + fechaDevolucion + "\n" +
                        "Estado: " + estado + "\n" +
                        "Días Restantes: " + diasRestantes;
        
        JOptionPane.showMessageDialog(this, mensaje, "Detalles del Préstamo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void registrarDevolucion() {
        int filaSeleccionada = tablaPrestamos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un préstamo para registrar devolución", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idPrestamo = (int) modeloTablaPrestamos.getValueAt(filaSeleccionada, 0);
        String libro = (String) modeloTablaPrestamos.getValueAt(filaSeleccionada, 1);
        String usuario = (String) modeloTablaPrestamos.getValueAt(filaSeleccionada, 2);
        
        String estado = (String) modeloTablaPrestamos.getValueAt(filaSeleccionada, 5);
        if (!"ACTIVO".equals(estado)) {
            JOptionPane.showMessageDialog(this, "Solo se pueden registrar devoluciones de préstamos ACTIVOS", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Registrar devolución del préstamo?\n\n" +
            "Libro: " + libro + "\n" +
            "Usuario: " + usuario,
            "Confirmar Devolución",
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            PrestamoDAO prestamoDAO = new PrestamoDAO();
            if (prestamoDAO.registrarDevolucion(idPrestamo)) {
                JOptionPane.showMessageDialog(this, "Devolución registrada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarPrestamos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar devolución", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportarReportePrestamos() {
        PrestamoDAO prestamoDAO = new PrestamoDAO();
        List<Prestamo> prestamos = prestamoDAO.obtenerTodosPrestamos();
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE PRÉSTAMOS - BiblioMasters\n");
        reporte.append("=====================================\n\n");
        
        for (Prestamo prestamo : prestamos) {
            reporte.append("ID: ").append(prestamo.getIdPrestamo()).append("\n");
            reporte.append("Libro: ").append(prestamo.getTituloLibro()).append("\n");
            reporte.append("Usuario: ").append(prestamo.getNombreUsuario()).append("\n");
            reporte.append("Fecha Préstamo: ").append(prestamo.getFechaPrestamo()).append("\n");
            reporte.append("Fecha Devolución: ").append(prestamo.getFechaDevolucionEstimada()).append("\n");
            reporte.append("Estado: ").append(prestamo.getEstado()).append("\n");
            reporte.append("-------------------------------------\n");
        }
        
        JTextArea textArea = new JTextArea(reporte.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Reporte de Préstamos", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // MÉTODOS PARA REPORTES
    private void generarReporteLibros() {
        LibroDAO libroDAO = new LibroDAO();
        List<Libro> libros = libroDAO.obtenerTodosLibros();
        
        long disponibles = libros.stream().filter(l -> "DISPONIBLE".equals(l.getEstado())).count();
        long prestados = libros.stream().filter(l -> "PRESTADO".equals(l.getEstado())).count();
        long mantenimiento = libros.stream().filter(l -> "MANTENIMIENTO".equals(l.getEstado())).count();
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE LIBROS - BiblioMasters\n");
        reporte.append("=================================\n\n");
        reporte.append("Total de Libros: ").append(libros.size()).append("\n");
        reporte.append("Disponibles: ").append(disponibles).append("\n");
        reporte.append("Prestados: ").append(prestados).append("\n");
        reporte.append("En Mantenimiento: ").append(mantenimiento).append("\n\n");
        reporte.append("DETALLE POR CATEGORÍA:\n");
        
        // Agrupar por categoría
        java.util.Map<String, Long> porCategoria = libros.stream()
            .collect(java.util.stream.Collectors.groupingBy(Libro::getCategoria, java.util.stream.Collectors.counting()));
        
        for (java.util.Map.Entry<String, Long> entry : porCategoria.entrySet()) {
            reporte.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        JTextArea textArea = new JTextArea(reporte.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Reporte de Libros", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void generarReporteUsuarios() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> usuarios = usuarioDAO.obtenerTodosUsuarios();
        
        long estudiantes = usuarios.stream().filter(u -> "ESTUDIANTE".equals(u.getTipoUsuario())).count();
        long docentes = usuarios.stream().filter(u -> "DOCENTE".equals(u.getTipoUsuario())).count();
        long bibliotecarios = usuarios.stream().filter(u -> "BIBLIOTECARIO".equals(u.getTipoUsuario())).count();
        long administradores = usuarios.stream().filter(u -> "ADMINISTRADOR".equals(u.getTipoUsuario())).count();
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE USUARIOS - BiblioMasters\n");
        reporte.append("===================================\n\n");
        reporte.append("Total de Usuarios: ").append(usuarios.size()).append("\n");
        reporte.append("Estudiantes: ").append(estudiantes).append("\n");
        reporte.append("Docentes: ").append(docentes).append("\n");
        reporte.append("Bibliotecarios: ").append(bibliotecarios).append("\n");
        reporte.append("Administradores: ").append(administradores).append("\n\n");
        reporte.append("ÚLTIMOS REGISTROS:\n");
        
        // Mostrar últimos 5 usuarios
        usuarios.stream().limit(5).forEach(u -> {
            reporte.append("  ").append(u.getNombre()).append(" (").append(u.getEmail()).append(") - ").append(u.getFechaRegistro()).append("\n");
        });
        
        JTextArea textArea = new JTextArea(reporte.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Reporte de Usuarios", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void generarReportePrestamos() {
        PrestamoDAO prestamoDAO = new PrestamoDAO();
        List<Prestamo> prestamos = prestamoDAO.obtenerTodosPrestamos();
        
        long activos = prestamos.stream().filter(p -> "ACTIVO".equals(p.getEstado())).count();
        long completados = prestamos.stream().filter(p -> "COMPLETADO".equals(p.getEstado())).count();
        long vencidos = prestamos.stream().filter(p -> "VENCIDO".equals(p.getEstado())).count();
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE PRÉSTAMOS - BiblioMasters\n");
        reporte.append("====================================\n\n");
        reporte.append("Total de Préstamos: ").append(prestamos.size()).append("\n");
        reporte.append("Activos: ").append(activos).append("\n");
        reporte.append("Completados: ").append(completados).append("\n");
        reporte.append("Vencidos: ").append(vencidos).append("\n\n");
        reporte.append("PRÉSTAMOS ACTIVOS:\n");
        
        // Mostrar préstamos activos
        prestamos.stream()
            .filter(p -> "ACTIVO".equals(p.getEstado()))
            .forEach(p -> {
                String dias = calcularDiasRestantes(p.getFechaDevolucionEstimada());
                reporte.append("  ").append(p.getTituloLibro()).append(" - ").append(p.getNombreUsuario())
                      .append(" (").append(dias).append(")\n");
            });
        
        JTextArea textArea = new JTextArea(reporte.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Reporte de Préstamos", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // MÉTODOS DE AUTENTICACIÓN
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
}