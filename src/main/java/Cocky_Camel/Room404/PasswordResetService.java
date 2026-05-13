package Cocky_Camel.Room404;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {

    private static final long EXPIRY_MARGIN_MS = 0L;

    private final ConcurrentHashMap<String, ResetTokenData> tokensByEmail = new ConcurrentHashMap<>();

    public void storeToken(String email, String token, long expiryTime) {
        tokensByEmail.put(normalize(email), new ResetTokenData(token, expiryTime));
    }

    public boolean isTokenValid(String email, String token) {
        ResetTokenData tokenData = tokensByEmail.get(normalize(email));
        if (tokenData == null) {
            return false;
        }
        if (!tokenData.token().equals(token)) {
            return false;
        }
        return System.currentTimeMillis() <= tokenData.expiryTime() - EXPIRY_MARGIN_MS;
    }

    public void clearToken(String email) {
        tokensByEmail.remove(normalize(email));
    }

    private String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private record ResetTokenData(String token, long expiryTime) {}
}