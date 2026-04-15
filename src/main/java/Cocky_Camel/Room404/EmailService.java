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
        message.setText("Hola " + nombre + ",\n\n" +
                        "Has forzado la entrada al sistema de mensajería cifrada. " +
                        "Como consecuencia, se ha bloqueado el acceso a la 'System Update'.\n\n" +
                        "Para continuar, resuelve el siguiente enigma:\n" +
                        "--------------------------------------------------\n" +
                        "«Soy el número del vacío digital.\n" +
                        "Aparezco cuando buscas lo que no existe.\n" +
                        "Dos veces el mismo par, rodeando el nada.\n" +
                        "¿Cuál es el código del Error?»\n" +
                        "--------------------------------------------------\n\n" +
                        "Introduce la respuesta en la Terminal de Actualización (System Update).\n" +
                        "El tiempo corre.");
        mailSender.send(message);
    }
}


