package Cocky_Camel.Room404;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
 
@Service
public class EmailService {
 
    @Autowired
    private JavaMailSender mailSender;
 
    public void sendMalwareEmail(String toEmail, String nickname) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("room404-system@game.com");
        message.setTo(toEmail);
        message.setSubject("⚠️ [CRITICAL] PROTOCOLO DE RECUPERACIÓN DE DATOS");
        String nombre = (nickname != null) ? nickname : "Sujeto";
        
        // PISTA CÓDIGO: 3 7 2 8
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
}