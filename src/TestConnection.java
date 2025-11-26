import dao.DatabaseConnection;
import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("🧪 Probando conexión Java con MySQL...");
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            if (conn != null) {
                System.out.println("🎉 ¡CONEXIÓN EXITOSA!");
            } else {
                System.out.println("💥 No se pudo conectar");
            }
            
        } catch (Exception e) {
            System.err.println("💥 Error: " + e.getMessage());
        }
    }
}