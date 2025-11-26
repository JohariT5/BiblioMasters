package model;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String email;
    private String password;
    private String tipoUsuario;
    private String codigoIdentificacion;
    private String fechaRegistro;
    
    // Constructores
    public Usuario() {}
    
    public Usuario(String nombre, String email, String password, String tipoUsuario, String codigoIdentificacion) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.tipoUsuario = tipoUsuario;
        this.codigoIdentificacion = codigoIdentificacion;
    }
    
    public Usuario(int idUsuario, String nombre, String email, String password, String tipoUsuario, 
                   String codigoIdentificacion, String fechaRegistro) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.tipoUsuario = tipoUsuario;
        this.codigoIdentificacion = codigoIdentificacion;
        this.fechaRegistro = fechaRegistro;
    }
    
    // Getters y Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }
    
    public String getCodigoIdentificacion() { return codigoIdentificacion; }
    public void setCodigoIdentificacion(String codigoIdentificacion) { this.codigoIdentificacion = codigoIdentificacion; }
    
    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", tipo='" + tipoUsuario + '\'' +
                '}';
    }
    
    // Métodos útiles para verificar tipos de usuario
    public boolean esAdministrador() {
        return "ADMINISTRADOR".equals(tipoUsuario);
    }
    
    public boolean esBibliotecario() {
        return "BIBLIOTECARIO".equals(tipoUsuario);
    }
    
    public boolean esEstudiante() {
        return "ESTUDIANTE".equals(tipoUsuario);
    }
    
    public boolean esDocente() {
        return "DOCENTE".equals(tipoUsuario);
    }
}