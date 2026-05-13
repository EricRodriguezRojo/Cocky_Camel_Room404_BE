package Cocky_Camel.Room404;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
 
@Service
public class EmailService {
 
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;
 
    public void sendMalwareEmail(String toEmail, String nickname) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(toEmail);
        message.setSubject("⚠️ [CRITICAL] PROTOCOLO DE RECUPERACIÓN DE DATOS");
        String nombre = (nickname != null) ? nickname : "Sujeto";
        
        // 3 7 2 8
        message.setText("Hola " + nombre + ",\n\n" +
                        "Has forzado la entrada al sistema de mensajería cifrada. " +
                        "Como consecuencia, se ha bloqueado el acceso a la 'System Update'.\n\n" +
                        "The Architect ha dejado este fragmento en tu memoria caché:\n" +
                        "--------------------------------------------------\n" +
                        "«Los colores de luz principales que forman tu pantalla (RGB).\n" +
                        "Los días de la semana que vigilo cada uno de tus pasos.\n" +
                        "Los únicos símbolos que entiende esta máquina: encendido o apagado.\n" +
                        "Las patas de la araña que teje la gran Red mundial (Web).»\n" +
                        "--------------------------------------------------\n\n" +
                        "Introduce el código en la Terminal de Actualización (System Update).\n" +
                        "El tiempo corre.");
                        
        mailSender.send(message);
    }
    
        public void sendPasswordResetEmail(String toEmail, String nickname, String resetToken) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(toEmail);
            message.setSubject("🔐 Recuperar contraseña - Room 404");
            String nombre = (nickname != null) ? nickname : "Usuario";
        
            message.setText("Hola " + nombre + ",\n\n" +
                            "Has solicitado recuperar tu contraseña en Room 404.\n\n" +
                            "Código de recuperación:\n" +
                            resetToken + "\n\n" +
                            "Este código es válido por 1 hora.\n" +
                            "Si no has solicitado esto, ignora este email.\n\n" +
                            "Saludos,\n" +
                            "Sistema de Room 404");
                        
            mailSender.send(message);
        }
}