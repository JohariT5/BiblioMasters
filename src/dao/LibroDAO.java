package dao;

import model.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {
    
    // Método para obtener todos los libros
    public List<Libro> obtenerTodosLibros() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros ORDER BY id_libro";
        
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
                libros.add(libro);
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR al obtener libros: " + e.getMessage());
        }
        
        return libros;
    }
    
    // Método para buscar libro por ID
    public Libro buscarPorId(int idLibro) {
        String sql = "SELECT * FROM libros WHERE id_libro = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idLibro);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Libro libro = new Libro();
                libro.setIdLibro(rs.getInt("id_libro"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setAutor(rs.getString("autor"));
                libro.setIsbn(rs.getString("isbn"));
                libro.setCategoria(rs.getString("categoria"));
                libro.setEditorial(rs.getString("editorial"));
                libro.setAnioPublicacion(rs.getInt("anio_publicacion"));
                libro.setEstado(rs.getString("estado"));
                return libro;
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR al buscar libro por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    // Método para buscar libros por texto
    public List<Libro> buscarLibros(String textoBusqueda) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE " +
                    "titulo LIKE ? OR " +
                    "autor LIKE ? OR " +
                    "isbn LIKE ? OR " +
                    "categoria LIKE ? OR " +
                    "editorial LIKE ? " +
                    "ORDER BY titulo";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String parametroBusqueda = "%" + textoBusqueda + "%";
            pstmt.setString(1, parametroBusqueda);
            pstmt.setString(2, parametroBusqueda);
            pstmt.setString(3, parametroBusqueda);
            pstmt.setString(4, parametroBusqueda);
            pstmt.setString(5, parametroBusqueda);
            
            ResultSet rs = pstmt.executeQuery();
            
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
                libros.add(libro);
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR al buscar libros: " + e.getMessage());
        }
        
        return libros;
    }
    
    // Método para crear un nuevo libro (alias de agregarLibro)
    public boolean crearLibro(Libro libro) {
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
            System.err.println("ERROR al crear libro: " + e.getMessage());
            return false;
        }
    }
    
    // Método alias - agregarLibro (llama a crearLibro)
    public boolean agregarLibro(Libro libro) {
        return crearLibro(libro);
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
            System.err.println("ERROR al actualizar libro: " + e.getMessage());
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
            System.err.println("ERROR al eliminar libro: " + e.getMessage());
            return false;
        }
    }
    
    // Método para obtener libros disponibles
    public List<Libro> obtenerLibrosDisponibles() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE estado = 'DISPONIBLE' ORDER BY titulo";
        
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
                libros.add(libro);
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR al obtener libros disponibles: " + e.getMessage());
        }
        
        return libros;
    }
    
    // Método para actualizar estado del libro
    public boolean actualizarEstadoLibro(int idLibro, String estado) {
        String sql = "UPDATE libros SET estado = ? WHERE id_libro = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, estado);
            pstmt.setInt(2, idLibro);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("ERROR al actualizar estado del libro: " + e.getMessage());
            return false;
        }
    }
}