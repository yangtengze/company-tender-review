package cn.edu.sdua._db.ytz.company_tender_review.service.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordCodec {
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public boolean matches(String rawPassword, String storedHash) {
        if (storedHash == null || storedHash.isBlank()) {
            return false;
        }
        if (!isBcryptHash(storedHash)) {
            throw new IllegalArgumentException("password hash format invalid, bcrypt required");
        }
        return bCryptPasswordEncoder.matches(rawPassword, storedHash);
    }

    public String bcrypt(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    private boolean isBcryptHash(String hash) {
        return hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$");
    }
}
