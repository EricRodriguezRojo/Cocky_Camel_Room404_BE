package Cocky_Camel.Room404;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    
    private static final String SECRET_PHRASE = "Esta_Es_La_Llave_Maestra_De_Room404_No_La_Cambiamos_Nunca_987654321";
    
    private static final Key key = Keys.hmacShaKeyFor(SECRET_PHRASE.getBytes());
    
    private static final long EXPIRATION_TIME = 864000000;

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}