import dao.LibroDAO;
import model.Libro;
import java.util.List;

public class TestLibroCRUD {
    public static void main(String[] args) {
        System.out.println("🧪 Probando CRUD de Libros...");
        
        LibroDAO libroDAO = new LibroDAO();
        
        // 1. PROBAR AGREGAR LIBRO - USANDO CONSTRUCTOR CORRECTO
        System.out.println("\n1. Agregando libro de prueba...");
        Libro nuevoLibro = new Libro();
        nuevoLibro.setTitulo("Cien años de soledad");
        nuevoLibro.setAutor("Gabriel García Márquez");
        nuevoLibro.setIsbn("9788437604947");
        nuevoLibro.setCategoria("Novela");
        nuevoLibro.setEditorial("Editorial Sudamericana");
        nuevoLibro.setAnioPublicacion(1967);
        nuevoLibro.setEstado("DISPONIBLE");
        // codigoQR se deja como null por defecto
        
        boolean agregado = libroDAO.agregarLibro(nuevoLibro);
        if (agregado) {
            System.out.println("✅ Libro agregado correctamente");
        } else {
            System.out.println("❌ Error al agregar libro");
        }
        
        // 2. PROBAR OBTENER TODOS LOS LIBROS
        System.out.println("\n2. Obteniendo todos los libros...");
        List<Libro> libros = libroDAO.obtenerTodosLibros();
        System.out.println("📚 Total de libros en la base de datos: " + libros.size());
        
        for (Libro libro : libros) {
            System.out.println("   - ID: " + libro.getIdLibro() + 
                             " | " + libro.getTitulo() + 
                             " por " + libro.getAutor() +
                             " | Estado: " + libro.getEstado());
        }
        
        // 3. PROBAR BUSCAR POR ID (si hay libros)
        if (!libros.isEmpty()) {
            System.out.println("\n3. Buscando primer libro por ID...");
            int primerId = libros.get(0).getIdLibro();
            Libro libroEncontrado = libroDAO.buscarPorId(primerId);
            if (libroEncontrado != null) {
                System.out.println("✅ Libro encontrado: " + libroEncontrado.getTitulo());
                
                // 4. PROBAR ACTUALIZAR LIBRO
                System.out.println("\n4. Actualizando estado del libro...");
                libroEncontrado.setEstado("PRESTADO");
                boolean actualizado = libroDAO.actualizarLibro(libroEncontrado);
                if (actualizado) {
                    System.out.println("✅ Libro actualizado correctamente");
                } else {
                    System.out.println("❌ Error al actualizar libro");
                }
            } else {
                System.out.println("❌ Libro no encontrado");
            }
        }
        
        // Mostrar estado final
        System.out.println("\n5. Estado final de los libros:");
        List<Libro> librosFinal = libroDAO.obtenerTodosLibros();
        for (Libro libro : librosFinal) {
            System.out.println("   - " + libro.getTitulo() + " | Estado: " + libro.getEstado());
        }
        
        System.out.println("\n🎉 Prueba de CRUD completada");
    }
}