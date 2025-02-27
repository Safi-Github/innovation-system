package mcit.ddr.innovation.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class JwtUtilityClass {

    private static final String SECRET = "YCfSGDFu+xkCpm8iDAhkJy6VrtaFJE9X1uC5kkA9YVcfN0ARhcuMAeAsMpaLotYYx32HwDSAm7BEqOtpnUOHRA==";
    private static final long VALIDITY = TimeUnit.MINUTES.toMillis(30);

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(); // Fix: Map<String, Object> instead of String
        claims.put("iss", "https://secure.safi.com");
        claims.put("firstname", "safiullah");
        // ✅ Add roles to JWT
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles);

        return Jwts.builder()
                .setClaims(claims)  // Fix: Use setClaims instead of claims()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(VALIDITY)))
                .signWith(generateKey()) // Fix: Use signWith() directly
                .compact();
    }  

    private SecretKey generateKey() {
        byte[] decodedKey = Base64.getDecoder().decode(SECRET);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    // ✅ Extract user roles from JWT
    public List<String> extractRoles(String jwt) {
        Claims claims = getClaims(jwt);
        return claims.get("roles", List.class); // Retrieve roles as a list
    }

    public String extractUsername(String jwt) {
        Claims claims = getClaims(jwt);
        return claims.getSubject();
    }

    private Claims getClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(generateKey())  // ✅ Use setSigningKey() instead of verifyWith()
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
    

    public boolean isTokenValid(String jwt) {
        Claims claims = getClaims(jwt);
        return claims.getExpiration().after(Date.from(Instant.now()));
    }


}