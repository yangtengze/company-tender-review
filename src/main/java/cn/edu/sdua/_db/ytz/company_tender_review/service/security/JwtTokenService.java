package cn.edu.sdua._db.ytz.company_tender_review.service.security;

import cn.edu.sdua._db.ytz.company_tender_review.config.JwtProperties;
import cn.edu.sdua._db.ytz.company_tender_review.repository.model.UserAuthView;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenService {
    private static final String TOKEN_TYPE_CLAIM = "typ";
    private static final String ACCESS_TYPE = "access";
    private static final String REFRESH_TYPE = "refresh";

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(UserAuthView user) {
        return createToken(user, ACCESS_TYPE, jwtProperties.accessExpireSeconds());
    }

    public String createRefreshToken(UserAuthView user) {
        return createToken(user, REFRESH_TYPE, jwtProperties.refreshExpireSeconds());
    }

    public long getAccessExpireSeconds() {
        return jwtProperties.accessExpireSeconds();
    }

    public Long parseAccessUserId(String bearerToken) {
        Claims claims = parseClaims(parseBearerToken(bearerToken));
        ensureType(claims, ACCESS_TYPE);
        return Long.valueOf(claims.getSubject());
    }

    public Long parseRefreshUserId(String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        ensureType(claims, REFRESH_TYPE);
        return Long.valueOf(claims.getSubject());
    }

    private String createToken(UserAuthView user, String tokenType, long expireSeconds) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.id()))
                .claim("username", user.username())
                .claim("role", user.role())
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expireSeconds)))
                .signWith(signingKey)
                .compact();
    }

    private Claims parseClaims(String jwtToken) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new IllegalArgumentException("token invalid");
        }
    }

    private static void ensureType(Claims claims, String expectedType) {
        String actualType = claims.get(TOKEN_TYPE_CLAIM, String.class);
        if (!expectedType.equals(actualType)) {
            throw new IllegalArgumentException("token type invalid");
        }
    }

    private static String parseBearerToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization Bearer token required");
        }
        return header.substring(7).trim();
    }
}
