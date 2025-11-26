package service;

import dao.UsuarioDAO;
import model.Usuario;

public class AuthService {
    private static Usuario usuarioLogueado = null;
    
    // Método de login con email y contraseña
    public static boolean login(String email, String password) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.autenticarUsuario(email, password);
        
        if (usuario != null) {
            usuarioLogueado = usuario;
            System.out.println("✅ Login exitoso: " + usuario.getNombre() + " (" + usuario.getTipoUsuario() + ")");
            return true;
        } else {
            System.out.println("❌ Credenciales incorrectas para: " + email);
            return false;
        }
    }
    
    public static void logout() {
        usuarioLogueado = null;
        System.out.println("🔒 Sesión cerrada");
    }
    
    public static Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }
    
    public static boolean estaLogueado() {
        return usuarioLogueado != null;
    }
    
    public static boolean esAdministrador() {
        return estaLogueado() && usuarioLogueado.esAdministrador();
    }
    
    public static boolean esBibliotecario() {
        return estaLogueado() && usuarioLogueado.esBibliotecario();
    }
    
    public static boolean esEstudiante() {
        return estaLogueado() && usuarioLogueado.esEstudiante();
    }
    
    public static boolean esDocente() {
        return estaLogueado() && usuarioLogueado.esDocente();
    }
}