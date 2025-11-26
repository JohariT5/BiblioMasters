package service;

import java.awt.*;
import javax.swing.*; // ← AGREGAR ESTE IMPORT

public class ThemeService {
    // Colores principales del tema
    public static final Color COLOR_PRIMARIO = new Color(41, 128, 185);    // Azul profesional
    public static final Color COLOR_SECUNDARIO = new Color(52, 152, 219);  // Azul claro
    public static final Color COLOR_EXITO = new Color(39, 174, 96);        // Verde
    public static final Color COLOR_PELIGRO = new Color(231, 76, 60);      // Rojo
    public static final Color COLOR_ADVERTENCIA = new Color(243, 156, 18); // Naranja
    public static final Color COLOR_OSCURO = new Color(44, 62, 80);        // Azul oscuro
    public static final Color COLOR_CLARO = new Color(236, 240, 241);      // Gris claro
    
    // Fuentes
    public static Font fuenteTitulo() {
        return new Font("Segoe UI", Font.BOLD, 24);
    }
    
    public static Font fuenteSubtitulo() {
        return new Font("Segoe UI", Font.BOLD, 16);
    }
    
    public static Font fuenteNormal() {
        return new Font("Segoe UI", Font.PLAIN, 14);
    }
    
    public static Font fuentePequeña() {
        return new Font("Segoe UI", Font.PLAIN, 12);
    }
    
    // Método para crear botones con estilo consistente
    public static JButton crearBoton(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(fuenteNormal());
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return boton;
    }
    
    // Método para crear botones primarios
    public static JButton crearBotonPrimario(String texto) {
        return crearBoton(texto, COLOR_PRIMARIO);
    }
    
    // Método para crear botones secundarios
    public static JButton crearBotonSecundario(String texto) {
        return crearBoton(texto, COLOR_OSCURO);
    }
}