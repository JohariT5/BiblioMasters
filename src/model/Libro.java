package model;

public class Libro {
    private int idLibro;
    private String titulo;
    private String autor;
    private String isbn;
    private String categoria;
    private String editorial;
    private int anioPublicacion;
    private String estado;
    private String codigoQR;
    
    // Constructor vacío
    public Libro() {}
    
    // Constructor con parámetros básicos
    public Libro(String titulo, String autor, String categoria, String editorial) {
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.editorial = editorial;
        this.estado = "DISPONIBLE"; // Estado por defecto
    }
    
    // Constructor completo
    public Libro(int idLibro, String titulo, String autor, String isbn, 
                 String categoria, String editorial, int anioPublicacion, 
                 String estado, String codigoQR) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.categoria = categoria;
        this.editorial = editorial;
        this.anioPublicacion = anioPublicacion;
        this.estado = estado;
        this.codigoQR = codigoQR;
    }
    
    // GETTERS Y SETTERS
    public int getIdLibro() { return idLibro; }
    public void setIdLibro(int idLibro) { this.idLibro = idLibro; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }
    
    public int getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(int anioPublicacion) { this.anioPublicacion = anioPublicacion; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getCodigoQR() { return codigoQR; }
    public void setCodigoQR(String codigoQR) { this.codigoQR = codigoQR; }
    
    @Override
    public String toString() {
        return "Libro{" +
                "id=" + idLibro +
                ", título='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}