package service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class QRService {
    
    // Método para generar código QR de un libro
    public static boolean generarQRParaLibro(int idLibro, String titulo, String autor, String rutaGuardado) {
        try {
            // Datos que contendrá el QR
            String datosQR = "BIBLIOMASTERS|ID:" + idLibro + "|TITULO:" + titulo + "|AUTOR:" + autor;
            
            // Configurar el generador de QR
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(datosQR, BarcodeFormat.QR_CODE, 300, 300);
            
            // Configurar colores (fondo blanco, código negro)
            MatrixToImageConfig config = new MatrixToImageConfig(0xFF000000, 0xFFFFFFFF);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, config);
            
            // Crear directorio si no existe
            File directorio = new File("qrcodes");
            if (!directorio.exists()) {
                directorio.mkdirs();
            }
            
            // Guardar imagen
            File archivoQR = new File(rutaGuardado);
            ImageIO.write(qrImage, "PNG", archivoQR);
            
            System.out.println("✅ Código QR generado: " + rutaGuardado);
            return true;
            
        } catch (WriterException | IOException e) {
            System.err.println("❌ Error al generar QR: " + e.getMessage());
            return false;
        }
    }
    
    // Método para generar QR con nombre automático
    public static String generarQRParaLibro(int idLibro, String titulo, String autor) {
        String nombreArchivo = "qrcodes/libro_" + idLibro + "_qr.png";
        boolean generado = generarQRParaLibro(idLibro, titulo, autor, nombreArchivo);
        
        if (generado) {
            return nombreArchivo;
        } else {
            return null;
        }
    }
    
    // Método para verificar si existe un QR
    public static boolean existeQR(int idLibro) {
        String nombreArchivo = "qrcodes/libro_" + idLibro + "_qr.png";
        File archivo = new File(nombreArchivo);
        return archivo.exists();
    }
    
    // Método para obtener la ruta del QR de un libro
    public static String obtenerRutaQR(int idLibro) {
        String nombreArchivo = "qrcodes/libro_" + idLibro + "_qr.png";
        File archivo = new File(nombreArchivo);
        
        if (archivo.exists()) {
            return nombreArchivo;
        } else {
            return null;
        }
    }
}