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

    // ✅ 1 ngày = 24h * 60m * 60s * 1000ms = 86_400_000 ms
    private static final long ONE_DAY = 24 * 60 * 60 * 1000;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /** ✅ Tạo JWT token có thời hạn 1 ngày */
    public String generateToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getRole());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ONE_DAY)) // ✅ token sống 1 ngày
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** ✅ Lấy username từ token */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .setAllowedClockSkewSeconds(300) // ✅ cho phép lệch 5 phút
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /** ✅ Kiểm tra token hợp lệ */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .setAllowedClockSkewSeconds(300) // ✅ cho phép lệch 5 phút
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
