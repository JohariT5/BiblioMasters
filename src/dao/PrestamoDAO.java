package dao;

import model.Prestamo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {
    
    // Método para solicitar un préstamo (con verificación de disponibilidad)
    public boolean solicitarPrestamo(Prestamo prestamo) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // 1. Verificar que el libro esté disponible
            String verificarDisponibilidad = "SELECT estado FROM libros WHERE id_libro = ? AND estado = 'DISPONIBLE'";
            try (PreparedStatement pstmtVerificar = conn.prepareStatement(verificarDisponibilidad)) {
                pstmtVerificar.setInt(1, prestamo.getIdLibro());
                ResultSet rs = pstmtVerificar.executeQuery();
                
                if (!rs.next()) {
                    System.err.println("❌ El libro no está disponible para préstamo");
                    conn.rollback();
                    return false;
                }
            }
            
            // 2. Registrar el préstamo
            String sqlPrestamo = "INSERT INTO prestamos (id_libro, id_usuario, fecha_prestamo, fecha_devolucion_estimada, estado) VALUES (?, ?, ?, ?, 'ACTIVO')";
            try (PreparedStatement pstmtPrestamo = conn.prepareStatement(sqlPrestamo)) {
                pstmtPrestamo.setInt(1, prestamo.getIdLibro());
                pstmtPrestamo.setInt(2, prestamo.getIdUsuario());
                pstmtPrestamo.setString(3, prestamo.getFechaPrestamo());
                pstmtPrestamo.setString(4, prestamo.getFechaDevolucionEstimada());
                
                int filasAfectadas = pstmtPrestamo.executeUpdate();
                if (filasAfectadas == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // 3. Actualizar estado del libro a PRESTADO
            String actualizarLibro = "UPDATE libros SET estado = 'PRESTADO' WHERE id_libro = ?";
            try (PreparedStatement pstmtActualizar = conn.prepareStatement(actualizarLibro)) {
                pstmtActualizar.setInt(1, prestamo.getIdLibro());
                pstmtActualizar.executeUpdate();
            }
            
            conn.commit(); // Confirmar transacción
            System.out.println("✅ Préstamo registrado correctamente");
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("❌ Error al hacer rollback: " + ex.getMessage());
            }
            System.err.println("❌ Error al solicitar préstamo: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
    
    // Método para registrar una devolución
    public boolean registrarDevolucion(int idPrestamo) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Obtener información del préstamo
            String obtenerPrestamo = "SELECT id_libro FROM prestamos WHERE id_prestamo = ? AND estado = 'ACTIVO'";
            int idLibro = -1;
            
            try (PreparedStatement pstmtObtener = conn.prepareStatement(obtenerPrestamo)) {
                pstmtObtener.setInt(1, idPrestamo);
                ResultSet rs = pstmtObtener.executeQuery();
                
                if (rs.next()) {
                    idLibro = rs.getInt("id_libro");
                } else {
                    System.err.println("❌ Préstamo no encontrado o ya devuelto");
                    conn.rollback();
                    return false;
                }
            }
            
            // 2. Actualizar préstamo a COMPLETADO
            String actualizarPrestamo = "UPDATE prestamos SET estado = 'COMPLETADO', fecha_devolucion_real = CURDATE() WHERE id_prestamo = ?";
            try (PreparedStatement pstmtPrestamo = conn.prepareStatement(actualizarPrestamo)) {
                pstmtPrestamo.setInt(1, idPrestamo);
                int filasAfectadas = pstmtPrestamo.executeUpdate();
                
                if (filasAfectadas == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // 3. Actualizar libro a DISPONIBLE
            String actualizarLibro = "UPDATE libros SET estado = 'DISPONIBLE' WHERE id_libro = ?";
            try (PreparedStatement pstmtLibro = conn.prepareStatement(actualizarLibro)) {
                pstmtLibro.setInt(1, idLibro);
                pstmtLibro.executeUpdate();
            }
            
            conn.commit();
            System.out.println("✅ Devolución registrada correctamente");
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("❌ Error al hacer rollback: " + ex.getMessage());
            }
            System.err.println("❌ Error al registrar devolución: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
    
    // Método para obtener todos los préstamos activos
    public List<Prestamo> obtenerPrestamosActivos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = """
            SELECT p.*, l.titulo as titulo_libro, u.nombre as nombre_usuario 
            FROM prestamos p
            JOIN libros l ON p.id_libro = l.id_libro
            JOIN usuarios u ON p.id_usuario = u.id_usuario
            WHERE p.estado = 'ACTIVO'
            ORDER BY p.fecha_prestamo DESC
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Prestamo prestamo = new Prestamo();
                prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
                prestamo.setIdLibro(rs.getInt("id_libro"));
                prestamo.setIdUsuario(rs.getInt("id_usuario"));
                prestamo.setFechaPrestamo(rs.getString("fecha_prestamo"));
                prestamo.setFechaDevolucionEstimada(rs.getString("fecha_devolucion_estimada"));
                prestamo.setFechaDevolucionReal(rs.getString("fecha_devolucion_real"));
                prestamo.setEstado(rs.getString("estado"));
                prestamo.setTituloLibro(rs.getString("titulo_libro"));
                prestamo.setNombreUsuario(rs.getString("nombre_usuario"));
                
                prestamos.add(prestamo);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener préstamos activos: " + e.getMessage());
        }
        
        return prestamos;
    }
    
    // Método para obtener historial completo de préstamos
    public List<Prestamo> obtenerTodosPrestamos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = """
            SELECT p.*, l.titulo as titulo_libro, u.nombre as nombre_usuario 
            FROM prestamos p
            JOIN libros l ON p.id_libro = l.id_libro
            JOIN usuarios u ON p.id_usuario = u.id_usuario
            ORDER BY p.fecha_prestamo DESC
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Prestamo prestamo = new Prestamo();
                prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
                prestamo.setIdLibro(rs.getInt("id_libro"));
                prestamo.setIdUsuario(rs.getInt("id_usuario"));
                prestamo.setFechaPrestamo(rs.getString("fecha_prestamo"));
                prestamo.setFechaDevolucionEstimada(rs.getString("fecha_devolucion_estimada"));
                prestamo.setFechaDevolucionReal(rs.getString("fecha_devolucion_real"));
                prestamo.setEstado(rs.getString("estado"));
                prestamo.setTituloLibro(rs.getString("titulo_libro"));
                prestamo.setNombreUsuario(rs.getString("nombre_usuario"));
                
                prestamos.add(prestamo);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener préstamos: " + e.getMessage());
        }
        
        return prestamos;
    }
}