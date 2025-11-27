package service;

import dao.PrestamoDAO;
import dao.LibroDAO;
import dao.UsuarioDAO;
import model.Prestamo;
import model.Libro;
import model.Usuario;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PrestamoService {
    private PrestamoDAO prestamoDAO;
    private LibroDAO libroDAO;
    private UsuarioDAO usuarioDAO;
    
    public PrestamoService() {
        this.prestamoDAO = new PrestamoDAO();
        this.libroDAO = new LibroDAO();
        this.usuarioDAO = new UsuarioDAO();
    }
    
    /**
     * Solicitar un préstamo con validaciones completas
     */
    public String solicitarPrestamo(int idLibro, int idUsuario) {
        try {
            // 1. Validar que el libro existe
            Libro libro = libroDAO.buscarPorId(idLibro);
            if (libro == null) {
                return "❌ Error: El libro no existe";
            }
            
            // 2. Validar que el usuario existe
            Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
            if (usuario == null) {
                return "❌ Error: El usuario no existe";
            }
            
            // 3. Validar que el libro está disponible
            if (!"DISPONIBLE".equals(libro.getEstado())) {
                return "❌ Error: El libro no está disponible. Estado actual: " + libro.getEstado();
            }
            
            // 4. Validar que el usuario puede solicitar préstamos
            if (!usuario.esEstudiante() && !usuario.esDocente()) {
                return "❌ Error: Solo estudiantes y docentes pueden solicitar préstamos";
            }
            
            // 5. Verificar si el usuario tiene préstamos activos (opcional - para límites)
            if (prestamoDAO.tienePrestamosActivos(idUsuario)) {
                System.out.println("⚠️  El usuario tiene préstamos activos");
            }
            
            // 6. Crear el préstamo
            Prestamo prestamo = new Prestamo();
            prestamo.setIdLibro(idLibro);
            prestamo.setIdUsuario(idUsuario);
            
            // Fechas
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String fechaPrestamo = LocalDate.now().format(formatter);
            String fechaDevolucion = calcularFechaDevolucion(usuario.getTipoUsuario());
            
            prestamo.setFechaPrestamo(fechaPrestamo);
            prestamo.setFechaDevolucionEstimada(fechaDevolucion);
            
            // 7. Guardar en la base de datos
            boolean exito = prestamoDAO.crearPrestamo(prestamo);
            
            if (exito) {
                return "✅ Préstamo creado exitosamente\n" +
                       "📖 Libro: " + libro.getTitulo() + "\n" +
                       "👤 Usuario: " + usuario.getNombre() + "\n" +
                       "📅 Fecha de devolución: " + fechaDevolucion;
            } else {
                return "❌ Error al crear el préstamo en la base de datos";
            }
            
        } catch (Exception e) {
            return "❌ Error inesperado: " + e.getMessage();
        }
    }
    
    /**
     * Calcular fecha de devolución según el tipo de usuario
     */
    private String calcularFechaDevolucion(String tipoUsuario) {
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaDevolucion;
        
        switch (tipoUsuario) {
            case "ESTUDIANTE":
                fechaDevolucion = fechaActual.plusDays(15); // 15 días para estudiantes
                break;
            case "DOCENTE":
                fechaDevolucion = fechaActual.plusDays(30); // 30 días para docentes
                break;
            case "BIBLIOTECARIO":
                fechaDevolucion = fechaActual.plusDays(30); // 30 días para bibliotecarios
                break;
            default:
                fechaDevolucion = fechaActual.plusDays(15); // Por defecto 15 días
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return fechaDevolucion.format(formatter);
    }
    
    /**
     * Registrar devolución de un préstamo
     */
    public String registrarDevolucion(int idPrestamo) {
        try {
            // 1. Verificar que el préstamo existe y está activo
            Prestamo prestamo = prestamoDAO.buscarPorId(idPrestamo);
            if (prestamo == null) {
                return "❌ Error: El préstamo no existe";
            }
            
            if (!prestamo.estaActivo()) {
                return "❌ Error: El préstamo no está activo. Estado actual: " + prestamo.getEstado();
            }
            
            // 2. Registrar la devolución
            boolean exito = prestamoDAO.registrarDevolucion(idPrestamo);
            
            if (exito) {
                return "✅ Devolución registrada exitosamente\n" +
                       "📖 Préstamo #" + idPrestamo + " completado";
            } else {
                return "❌ Error al registrar la devolución";
            }
            
        } catch (Exception e) {
            return "❌ Error inesperado: " + e.getMessage();
        }
    }
    
    /**
     * Obtener todos los préstamos
     */
    public List<Prestamo> obtenerTodosPrestamos() {
        return prestamoDAO.obtenerTodosPrestamos();
    }
    
    /**
     * Obtener préstamos activos
     */
    public List<Prestamo> obtenerPrestamosActivos() {
        return prestamoDAO.obtenerPrestamosActivos();
    }
    
    /**
     * Obtener préstamos por usuario
     */
    public List<Prestamo> obtenerPrestamosPorUsuario(int usuarioId) {
        return prestamoDAO.obtenerPrestamosPorUsuario(usuarioId);
    }
    
    /**
     * Verificar si un libro está disponible
     */
    public boolean verificarDisponibilidadLibro(int idLibro) {
        Libro libro = libroDAO.buscarPorId(idLibro);
        return libro != null && "DISPONIBLE".equals(libro.getEstado());
    }
    
    /**
     * Obtener información de un préstamo
     */
    public Prestamo obtenerPrestamo(int idPrestamo) {
        return prestamoDAO.buscarPorId(idPrestamo);
    }
}