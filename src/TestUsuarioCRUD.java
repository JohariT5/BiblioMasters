import dao.UsuarioDAO;
import model.Usuario;
import java.util.List;

public class TestUsuarioCRUD {
    public static void main(String[] args) {
        System.out.println("🧪 Probando CRUD de Usuarios...");
        
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        
        // 1. PROBAR OBTENER TODOS LOS USUARIOS (los que insertamos por SQL)
        System.out.println("\n1. Obteniendo todos los usuarios...");
        List<Usuario> usuarios = usuarioDAO.obtenerTodosUsuarios();
        System.out.println("👥 Total de usuarios en la base de datos: " + usuarios.size());
        
        for (Usuario usuario : usuarios) {
            System.out.println("   - " + usuario.getNombre() + " (" + usuario.getTipoUsuario() + ")");
        }
        
        // 2. PROBAR AGREGAR NUEVO USUARIO
        System.out.println("\n2. Agregando nuevo usuario...");
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre("Carlos Estudiante");
        nuevoUsuario.setEmail("carlos.estudiante@universidad.edu");
        nuevoUsuario.setTipoUsuario("ESTUDIANTE");
        nuevoUsuario.setCodigoIdentificacion("EST002");
        
        boolean agregado = usuarioDAO.agregarUsuario(nuevoUsuario);
        if (agregado) {
            System.out.println("✅ Usuario agregado correctamente");
        } else {
            System.out.println("❌ Error al agregar usuario");
        }
        
        // 3. PROBAR BUSCAR POR EMAIL
        System.out.println("\n3. Buscando usuario por email...");
        Usuario usuarioEncontrado = usuarioDAO.buscarPorEmail("carlos.estudiante@universidad.edu");
        if (usuarioEncontrado != null) {
            System.out.println("✅ Usuario encontrado: " + usuarioEncontrado.getNombre());
            
            // 4. PROBAR ACTUALIZAR USUARIO
            System.out.println("\n4. Actualizando usuario...");
            usuarioEncontrado.setNombre("Carlos Martínez");
            boolean actualizado = usuarioDAO.actualizarUsuario(usuarioEncontrado);
            if (actualizado) {
                System.out.println("✅ Usuario actualizado correctamente");
            } else {
                System.out.println("❌ Error al actualizar usuario");
            }
        }
        
        // 5. PROBAR OBTENER USUARIOS POR TIPO
        System.out.println("\n5. Obteniendo solo estudiantes...");
        List<Usuario> estudiantes = usuarioDAO.obtenerUsuariosPorTipo("ESTUDIANTE");
        System.out.println("🎓 Total de estudiantes: " + estudiantes.size());
        for (Usuario est : estudiantes) {
            System.out.println("   - " + est.getNombre() + " - " + est.getCodigoIdentificacion());
        }
        
        // 6. MOSTRAR ESTADO FINAL
        System.out.println("\n6. Estado final de todos los usuarios:");
        List<Usuario> usuariosFinal = usuarioDAO.obtenerTodosUsuarios();
        for (Usuario usuario : usuariosFinal) {
            System.out.println("   - ID: " + usuario.getIdUsuario() + 
                             " | " + usuario.getNombre() + 
                             " | " + usuario.getTipoUsuario() +
                             " | " + usuario.getEmail());
        }
        
        System.out.println("\n🎉 Prueba de CRUD de usuarios completada");
    }
}