package dao;

import model.Prestamo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {
    
    // Metodo para crear un nuevo prestamo 
    public boolean crearPrestamo(Prestamo prestamo) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Iniciar transaccion
            
            // 1. Verificar que el libro este disponible
            String verificarLibro = "SELECT estado FROM libros WHERE id_libro = ?";
            PreparedStatement pstmtVerificar = conn.prepareStatement(verificarLibro);
            pstmtVerificar.setInt(1, prestamo.getIdLibro());
            ResultSet rs = pstmtVerificar.executeQuery();
            
            if (rs.next()) {
                String estadoLibro = rs.getString("estado");
                if (!"DISPONIBLE".equals(estadoLibro)) {
                    System.err.println("ERROR: El libro no esta disponible para prestamo");
                    conn.rollback();
                    return false;
                }
            } else {
                System.err.println("ERROR: Libro no encontrado");
                conn.rollback();
                return false;
            }
            
            // 2. Insertar el prestamo
            String sqlPrestamo = "INSERT INTO prestamos (id_usuario, id_libro, fecha_prestamo, fecha_devolucion_estimada, estado) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmtPrestamo = conn.prepareStatement(sqlPrestamo);
            pstmtPrestamo.setInt(1, prestamo.getIdUsuario());
            pstmtPrestamo.setInt(2, prestamo.getIdLibro());
            pstmtPrestamo.setString(3, prestamo.getFechaPrestamo());
            pstmtPrestamo.setString(4, prestamo.getFechaDevolucionEstimada());
            pstmtPrestamo.setString(5, "ACTIVO");
            
            int filasPrestamo = pstmtPrestamo.executeUpdate();
            
            if (filasPrestamo <= 0) {
                conn.rollback();
                return false;
            }
            
            // 3. Actualizar estado del libro a PRESTADO
            String actualizarLibro = "UPDATE libros SET estado = 'PRESTADO' WHERE id_libro = ?";
            PreparedStatement pstmtLibro = conn.prepareStatement(actualizarLibro);
            pstmtLibro.setInt(1, prestamo.getIdLibro());
            int filasLibro = pstmtLibro.executeUpdate();
            
            if (filasLibro <= 0) {
                conn.rollback();
                return false;
            }
            
            // Si todo salio bien, confirmar la transaccion
            conn.commit();
            System.out.println("EXITO: Prestamo creado exitosamente");
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Revertir en caso de error
                }
            } catch (SQLException rollbackEx) {
                System.err.println("ERROR al hacer rollback: " + rollbackEx.getMessage());
            }
            System.err.println("ERROR al crear prestamo: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Restaurar auto-commit
                }
            } catch (SQLException e) {
                System.err.println("ERROR al restaurar auto-commit: " + e.getMessage());
            }
        }
    }
    
    // Metodo para buscar prestamo por ID
    public Prestamo buscarPorId(int idPrestamo) {
        String sql = "SELECT p.*, l.titulo as titulo_libro, u.nombre as nombre_usuario " +
                    "FROM prestamos p " +
                    "JOIN libros l ON p.id_libro = l.id_libro " +
                    "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                    "WHERE p.id_prestamo = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPrestamo);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Prestamo prestamo = new Prestamo();
                prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
                prestamo.setIdUsuario(rs.getInt("id_usuario"));
                prestamo.setIdLibro(rs.getInt("id_libro"));
                prestamo.setFechaPrestamo(rs.getString("fecha_prestamo"));
                prestamo.setFechaDevolucionEstimada(rs.getString("fecha_devolucion_estimada"));
                prestamo.setFechaDevolucionReal(rs.getString("fecha_devolucion_real"));
                prestamo.setEstado(rs.getString("estado"));
                prestamo.setTituloLibro(rs.getString("titulo_libro"));
                prestamo.setNombreUsuario(rs.getString("nombre_usuario"));
                return prestamo;
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR al buscar prestamo: " + e.getMessage());
        }
        
        return null;
    }
    
    // Metodo para registrar devolucion
    public boolean registrarDevolucion(int idPrestamo) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Actualizar el prestamo a COMPLETADO
            String sqlPrestamo = "UPDATE prestamos SET estado = 'COMPLETADO', fecha_devolucion_real = CURRENT_DATE WHERE id_prestamo = ?";
            PreparedStatement pstmtPrestamo = conn.prepareStatement(sqlPrestamo);
            pstmtPrestamo.setInt(1, idPrestamo);
            int filasPrestamo = pstmtPrestamo.executeUpdate();
            
            if (filasPrestamo <= 0) {
                conn.rollback();
                return false;
            }
            
            // 2. Obtener el ID del libro para actualizar su estado
            String obtenerLibro = "SELECT id_libro FROM prestamos WHERE id_prestamo = ?";
            PreparedStatement pstmtObtener = conn.prepareStatement(obtenerLibro);
            pstmtObtener.setInt(1, idPrestamo);
            ResultSet rs = pstmtObtener.executeQuery();
            
            if (rs.next()) {
                int idLibro = rs.getInt("id_libro");
                
                // 3. Actualizar el estado del libro a DISPONIBLE
                String actualizarLibro = "UPDATE libros SET estado = 'DISPONIBLE' WHERE id_libro = ?";
                PreparedStatement pstmtLibro = conn.prepareStatement(actualizarLibro);
                pstmtLibro.setInt(1, idLibro);
                int filasLibro = pstmtLibro.executeUpdate();
                
                if (filasLibro <= 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            System.out.println("EXITO: Devolucion registrada exitosamente");
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("ERROR al hacer rollback: " + rollbackEx.getMessage());
            }
            System.err.println("ERROR al registrar devolucion: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("ERROR al restaurar auto-commit: " + e.getMessage());
            }
        }
    }
    
    // Metodos existentes (preservados)
    public List<Prestamo> obtenerTodosPrestamos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, l.titulo as titulo_libro, u.nombre as nombre_usuario " +
                    "FROM prestamos p " +
                    "JOIN libros l ON p.id_libro = l.id_libro " +
                    "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                    "ORDER BY p.fecha_prestamo DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Prestamo prestamo = new Prestamo();
                prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
                prestamo.setIdUsuario(rs.getInt("id_usuario"));
                prestamo.setIdLibro(rs.getInt("id_libro"));
                prestamo.setFechaPrestamo(rs.getString("fecha_prestamo"));
                prestamo.setFechaDevolucionEstimada(rs.getString("fecha_devolucion_estimada"));
                prestamo.setFechaDevolucionReal(rs.getString("fecha_devolucion_real"));
                prestamo.setEstado(rs.getString("estado"));
                prestamo.setTituloLibro(rs.getString("titulo_libro"));
                prestamo.setNombreUsuario(rs.getString("nombre_usuario"));
                prestamos.add(prestamo);
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR al obtener prestamos: " + e.getMessage());
        }
        
        return prestamos;
    }
    
    public List<Prestamo> obtenerPrestamosActivos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, l.titulo as titulo_libro, u.nombre as nombre_usuario " +
                    "FROM prestamos p " +
                    "JOIN libros l ON p.id_libro = l.id_libro " +
                    "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                    "WHERE p.estado = 'ACTIVO' " +
                    "ORDER BY p.fecha_prestamo DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Prestamo prestamo = new Prestamo();
                prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
                prestamo.setIdUsuario(rs.getInt("id_usuario"));
                prestamo.setIdLibro(rs.getInt("id_libro"));
                prestamo.setFechaPrestamo(rs.getString("fecha_prestamo"));
                prestamo.setFechaDevolucionEstimada(rs.getString("fecha_devolucion_estimada"));
                prestamo.setFechaDevolucionReal(rs.getString("fecha_devolucion_real"));
                prestamo.setEstado(rs.getString("estado"));
                prestamo.setTituloLibro(rs.getString("titulo_libro"));
                prestamo.setNombreUsuario(rs.getString("nombre_usuario"));
                prestamos.add(prestamo);
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR al obtener prestamos activos: " + e.getMessage());
        }
        
        return prestamos;
    }
    
    public List<Prestamo> obtenerPrestamosPorUsuario(int usuarioId) {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, l.titulo as titulo_libro, u.nombre as nombre_usuario " +
                    "FROM prestamos p " +
                    "JOIN libros l ON p.id_libro = l.id_libro " +
                    "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                    "WHERE p.id_usuario = ? " +
                    "ORDER BY p.fecha_prestamo DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Prestamo prestamo = new Prestamo();
                prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
                prestamo.setIdUsuario(rs.getInt("id_usuario"));
                prestamo.setIdLibro(rs.getInt("id_libro"));
                prestamo.setFechaPrestamo(rs.getString("fecha_prestamo"));
                prestamo.setFechaDevolucionEstimada(rs.getString("fecha_devolucion_estimada"));
                prestamo.setFechaDevolucionReal(rs.getString("fecha_devolucion_real"));
                prestamo.setEstado(rs.getString("estado"));
                prestamo.setTituloLibro(rs.getString("titulo_libro"));
                prestamo.setNombreUsuario(rs.getString("nombre_usuario"));
                prestamos.add(prestamo);
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR al obtener prestamos por usuario: " + e.getMessage());
        }
        
        return prestamos;
    }
    
    // Metodo adicional: Verificar si un usuario tiene prestamos activos
    public boolean tienePrestamosActivos(int usuarioId) {
        String sql = "SELECT COUNT(*) as total FROM prestamos WHERE id_usuario = ? AND estado = 'ACTIVO'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR al verificar prestamos activos: " + e.getMessage());
        }
        
        return false;
    }
}