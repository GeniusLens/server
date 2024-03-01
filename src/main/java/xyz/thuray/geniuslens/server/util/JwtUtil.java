package xyz.thuray.geniuslens.server.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xyz.thuray.geniuslens.server.data.po.AuthPO;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final static SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;
    public static final SecretKey KEY = Keys.hmacShaKeyFor("TMPTMPTMPTMPTMPTMPTMPTMPTMPTMPTMPTMPTMPTMPTMPTMPTMPTMPTMPTMPTMPTMP".getBytes());
    private static final long EXPIRATION_TIME = 86400000 * 3; // 24小时

    public static String generateToken(AuthPO po) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .subject(String.valueOf(po.getUserId()))
                .signWith(KEY, ALGORITHM)
                .issuedAt(now)
                .expiration(expiration)
                .compact();
    }

    public static String verifyToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            log.error("verify token error: {}", e.getMessage());
            return null;
        }
    }

    public static Long parseToken(String token) {
        // 取出subject
        String subject = verifyToken(token);
        if (subject == null) {
            return null;
        }
        log.info("subject: {}", subject);
        return Long.parseLong(subject);
    }
}

