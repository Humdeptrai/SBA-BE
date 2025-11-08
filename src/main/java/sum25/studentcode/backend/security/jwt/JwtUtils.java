package sum25.studentcode.backend.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sum25.studentcode.backend.model.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private static final long ONE_DAY = 24 * 60 * 60 * 1000;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getRole());
        claims.put("userId", userDetails.getUserId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ONE_DAY))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .setAllowedClockSkewSeconds(300)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Long extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .setAllowedClockSkewSeconds(300)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Lấy userId từ claims và convert sang Long
        Object userIdClaim = claims.get("userId");
        if (userIdClaim != null) {
            if (userIdClaim instanceof Integer) {
                return ((Integer) userIdClaim).longValue();
            } else if (userIdClaim instanceof Long) {
                return (Long) userIdClaim;
            } else if (userIdClaim instanceof String) {
                return Long.valueOf((String) userIdClaim);
            }
        }
        return null;
    }

    public String extractRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .setAllowedClockSkewSeconds(300)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return (String) claims.get("roles");
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .setAllowedClockSkewSeconds(300)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("❌ Token đã hết hạn: " + e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("❌ Token không hợp lệ: " + e.getMessage());
        }
        return false;
    }
}
