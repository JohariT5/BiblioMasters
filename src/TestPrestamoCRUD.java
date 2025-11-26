import dao.PrestamoDAO;
import dao.LibroDAO;
import dao.UsuarioDAO;
import model.Prestamo;
import model.Libro;
import model.Usuario;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class TestPrestamoCRUD {
    public static void main(String[] args) {
        System.out.println("🧪 Probando Sistema de Préstamos...");
        
        PrestamoDAO prestamoDAO = new PrestamoDAO();
        LibroDAO libroDAO = new LibroDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        
        // Obtener fecha actual y fecha de devolución (15 días después)
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 15);
        String fechaDevolucion = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        
        // 1. OBTENER LIBROS DISPONIBLES Y USUARIOS
        System.out.println("\n1. Buscando libros disponibles y usuarios...");
        List<Libro> libros = libroDAO.obtenerTodosLibros();
        List<Usuario> usuarios = usuarioDAO.obtenerTodosUsuarios();
        
        // Encontrar un libro disponible y un usuario estudiante
        Libro libroParaPrestar = null;
        Usuario usuarioParaPrestar = null;
        
        for (Libro libro : libros) {
            if ("DISPONIBLE".equals(libro.getEstado())) {
                libroParaPrestar = libro;
                break;
            }
        }
        
        for (Usuario usuario : usuarios) {
            if ("ESTUDIANTE".equals(usuario.getTipoUsuario())) {
                usuarioParaPrestar = usuario;
                break;
            }
        }
        
        if (libroParaPrestar == null || usuarioParaPrestar == null) {
            System.out.println("❌ No se encontró libro disponible o usuario estudiante");
            return;
        }
        
        System.out.println("📖 Libro para préstamo: " + libroParaPrestar.getTitulo());
        System.out.println("👤 Usuario para préstamo: " + usuarioParaPrestar.getNombre());
        
        // 2. SOLICITAR PRÉSTAMO
        System.out.println("\n2. Solicitando préstamo...");
        Prestamo nuevoPrestamo = new Prestamo(
            libroParaPrestar.getIdLibro(),
            usuarioParaPrestar.getIdUsuario(),
            fechaActual,
            fechaDevolucion
        );
        
        boolean prestamoSolicitado = prestamoDAO.solicitarPrestamo(nuevoPrestamo);
        if (prestamoSolicitado) {
            System.out.println("✅ Préstamo solicitado correctamente");
        } else {
            System.out.println("❌ Error al solicitar préstamo");
        }
        
        // 3. VERIFICAR PRÉSTAMOS ACTIVOS
        System.out.println("\n3. Préstamos activos en el sistema:");
        List<Prestamo> prestamosActivos = prestamoDAO.obtenerPrestamosActivos();
        System.out.println("📋 Total de préstamos activos: " + prestamosActivos.size());
        
        for (Prestamo prestamo : prestamosActivos) {
            System.out.println("   - ID: " + prestamo.getIdPrestamo() + 
                             " | Libro: " + prestamo.getTituloLibro() +
                             " | Usuario: " + prestamo.getNombreUsuario() +
                             " | Fecha préstamo: " + prestamo.getFechaPrestamo());
        }
        
        // 4. VERIFICAR ESTADO DEL LIBRO (debería estar PRESTADO)
        System.out.println("\n4. Verificando estado del libro...");
        Libro libroActualizado = libroDAO.buscarPorId(libroParaPrestar.getIdLibro());
        if (libroActualizado != null) {
            System.out.println("📖 Estado del libro '" + libroActualizado.getTitulo() + "': " + libroActualizado.getEstado());
        }
        
        // 5. REGISTRAR DEVOLUCIÓN (si hay préstamos activos)
        if (!prestamosActivos.isEmpty()) {
            System.out.println("\n5. Registrando devolución...");
            int idPrestamo = prestamosActivos.get(0).getIdPrestamo();
            boolean devolucionRegistrada = prestamoDAO.registrarDevolucion(idPrestamo);
            
            if (devolucionRegistrada) {
                System.out.println("✅ Devolución registrada correctamente");
            } else {
                System.out.println("❌ Error al registrar devolución");
            }
            
            // 6. VERIFICAR ESTADO FINAL
            System.out.println("\n6. Estado final del libro...");
            Libro libroFinal = libroDAO.buscarPorId(libroParaPrestar.getIdLibro());
            if (libroFinal != null) {
                System.out.println("📖 Estado final del libro '" + libroFinal.getTitulo() + "': " + libroFinal.getEstado());
            }
            
            // 7. MOSTRAR HISTORIAL COMPLETO
            System.out.println("\n7. Historial completo de préstamos:");
            List<Prestamo> todosPrestamos = prestamoDAO.obtenerTodosPrestamos();
            for (Prestamo prestamo : todosPrestamos) {
                System.out.println("   - ID: " + prestamo.getIdPrestamo() + 
                                 " | Libro: " + prestamo.getTituloLibro() +
                                 " | Usuario: " + prestamo.getNombreUsuario() +
                                 " | Estado: " + prestamo.getEstado() +
                                 " | Fecha devolución: " + (prestamo.getFechaDevolucionReal() != null ? prestamo.getFechaDevolucionReal() : "Pendiente"));
            }
        }
        
        System.out.println("\n🎉 Prueba del sistema de préstamos completada");
    }
}