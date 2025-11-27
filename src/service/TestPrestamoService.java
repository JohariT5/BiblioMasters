package service;

import model.Prestamo;
import java.util.List;

public class TestPrestamoService {
    public static void main(String[] args) {
        PrestamoService prestamoService = new PrestamoService();
        
        System.out.println("🧪 INICIANDO PRUEBAS DEL SERVICIO DE PRÉSTAMOS\n");
        
        // Prueba 1: Solicitar un préstamo válido
        System.out.println("1. 📖 SOLICITANDO PRÉSTAMO VÁLIDO");
        System.out.println("   Libro ID: 1, Usuario ID: 1 (Estudiante)");
        String resultado1 = prestamoService.solicitarPrestamo(1, 1);
        System.out.println("   Resultado: " + resultado1);
        System.out.println();
        
        // Prueba 2: Intentar préstamo con libro no disponible
        System.out.println("2. 📖 INTENTANDO PRÉSTAMO CON LIBRO NO DISPONIBLE");
        System.out.println("   Libro ID: 1, Usuario ID: 4");
        String resultado2 = prestamoService.solicitarPrestamo(1, 4);
        System.out.println("   Resultado: " + resultado2);
        System.out.println();
        
        // Prueba 3: Intentar préstamo con usuario que no existe
        System.out.println("3. 📖 INTENTANDO PRÉSTAMO CON USUARIO INEXISTENTE");
        System.out.println("   Libro ID: 2, Usuario ID: 999");
        String resultado3 = prestamoService.solicitarPrestamo(2, 999);
        System.out.println("   Resultado: " + resultado3);
        System.out.println();
        
        // Prueba 4: Ver préstamos activos
        System.out.println("4. 📋 LISTANDO PRÉSTAMOS ACTIVOS");
        List<Prestamo> prestamosActivos = prestamoService.obtenerPrestamosActivos();
        if (prestamosActivos.isEmpty()) {
            System.out.println("   No hay préstamos activos");
        } else {
            for (Prestamo p : prestamosActivos) {
                System.out.println("   - Préstamo #" + p.getIdPrestamo() + 
                                 ": " + p.getTituloLibro() + " → " + p.getNombreUsuario() +
                                 " (Devolución: " + p.getFechaDevolucionEstimada() + ")");
            }
        }
        System.out.println();
        
        // Prueba 5: Registrar devolución (si hay préstamos activos)
        if (!prestamosActivos.isEmpty()) {
            System.out.println("5. 🔄 REGISTRANDO DEVOLUCIÓN");
            int primerPrestamoId = prestamosActivos.get(0).getIdPrestamo();
            String resultadoDevolucion = prestamoService.registrarDevolucion(primerPrestamoId);
            System.out.println("   Resultado: " + resultadoDevolucion);
        }
        
        System.out.println("\n✅ PRUEBAS COMPLETADAS");
    }
}