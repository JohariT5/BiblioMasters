package model;

public class Prestamo {
    private int idPrestamo;
    private int idLibro;
    private int idUsuario;
    private String fechaPrestamo;
    private String fechaDevolucionEstimada;
    private String fechaDevolucionReal;
    private String estado;
    
    // Información adicional (para mostrar en interfaces)
    private String tituloLibro;
    private String nombreUsuario;
    
    // Constructores
    public Prestamo() {}
    
    public Prestamo(int idLibro, int idUsuario, String fechaPrestamo, String fechaDevolucionEstimada) {
        this.idLibro = idLibro;
        this.idUsuario = idUsuario;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEstimada = fechaDevolucionEstimada;
        this.estado = "ACTIVO";
    }
    
    public Prestamo(int idPrestamo, int idLibro, int idUsuario, String fechaPrestamo, 
                   String fechaDevolucionEstimada, String fechaDevolucionReal, String estado) {
        this.idPrestamo = idPrestamo;
        this.idLibro = idLibro;
        this.idUsuario = idUsuario;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEstimada = fechaDevolucionEstimada;
        this.fechaDevolucionReal = fechaDevolucionReal;
        this.estado = estado;
    }
    
    // Getters y Setters
    public int getIdPrestamo() { return idPrestamo; }
    public void setIdPrestamo(int idPrestamo) { this.idPrestamo = idPrestamo; }
    
    public int getIdLibro() { return idLibro; }
    public void setIdLibro(int idLibro) { this.idLibro = idLibro; }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public String getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(String fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    
    public String getFechaDevolucionEstimada() { return fechaDevolucionEstimada; }
    public void setFechaDevolucionEstimada(String fechaDevolucionEstimada) { this.fechaDevolucionEstimada = fechaDevolucionEstimada; }
    
    public String getFechaDevolucionReal() { return fechaDevolucionReal; }
    public void setFechaDevolucionReal(String fechaDevolucionReal) { this.fechaDevolucionReal = fechaDevolucionReal; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getTituloLibro() { return tituloLibro; }
    public void setTituloLibro(String tituloLibro) { this.tituloLibro = tituloLibro; }
    
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    
    // Métodos útiles
    public boolean estaActivo() {
        return "ACTIVO".equals(estado);
    }
    
    public boolean estaCompletado() {
        return "COMPLETADO".equals(estado);
    }
    
    public boolean estaVencido() {
        return "VENCIDO".equals(estado);
    }
    
    @Override
    public String toString() {
        return "Préstamo{" +
                "id=" + idPrestamo +
                ", libro=" + idLibro +
                ", usuario=" + idUsuario +
                ", estado='" + estado + '\'' +
                '}';
    }
}