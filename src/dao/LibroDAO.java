package dao;

import model.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {
    
    // Método para agregar un nuevo libro
    public boolean agregarLibro(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, isbn, categoria, editorial, anio_publicacion, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, libro.getTitulo());
            pstmt.setString(2, libro.getAutor());
            pstmt.setString(3, libro.getIsbn());
            pstmt.setString(4, libro.getCategoria());
            pstmt.setString(5, libro.getEditorial());
            pstmt.setInt(6, libro.getAnioPublicacion());
            pstmt.setString(7, libro.getEstado());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al agregar libro: " + e.getMessage());
            return false;
        }
    }
    
    // Método para obtener todos los libros
    public List<Libro> obtenerTodosLibros() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Libro libro = new Libro();
                libro.setIdLibro(rs.getInt("id_libro"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setAutor(rs.getString("autor"));
                libro.setIsbn(rs.getString("isbn"));
                libro.setCategoria(rs.getString("categoria"));
                libro.setEditorial(rs.getString("editorial"));
                libro.setAnioPublicacion(rs.getInt("anio_publicacion"));
                libro.setEstado(rs.getString("estado"));
                libro.setCodigoQR(rs.getString("codigo_qr"));
                
                libros.add(libro);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener libros: " + e.getMessage());
        }
        
        return libros;
    }
    
    // Método para buscar libro por ID
    public Libro buscarPorId(int idLibro) {
        String sql = "SELECT * FROM libros WHERE id_libro = ?";
        Libro libro = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idLibro);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                libro = new Libro();
                libro.setIdLibro(rs.getInt("id_libro"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setAutor(rs.getString("autor"));
                libro.setIsbn(rs.getString("isbn"));
                libro.setCategoria(rs.getString("categoria"));
                libro.setEditorial(rs.getString("editorial"));
                libro.setAnioPublicacion(rs.getInt("anio_publicacion"));
                libro.setEstado(rs.getString("estado"));
                libro.setCodigoQR(rs.getString("codigo_qr"));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar libro: " + e.getMessage());
        }
        
        return libro;
    }
    
    // Método para actualizar un libro
    public boolean actualizarLibro(Libro libro) {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, isbn = ?, categoria = ?, editorial = ?, anio_publicacion = ?, estado = ? WHERE id_libro = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, libro.getTitulo());
            pstmt.setString(2, libro.getAutor());
            pstmt.setString(3, libro.getIsbn());
            pstmt.setString(4, libro.getCategoria());
            pstmt.setString(5, libro.getEditorial());
            pstmt.setInt(6, libro.getAnioPublicacion());
            pstmt.setString(7, libro.getEstado());
            pstmt.setInt(8, libro.getIdLibro());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar libro: " + e.getMessage());
            return false;
        }
    }
    
    // Método para eliminar un libro
    public boolean eliminarLibro(int idLibro) {
        String sql = "DELETE FROM libros WHERE id_libro = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idLibro);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar libro: " + e.getMessage());
            return false;
        }
    }
}