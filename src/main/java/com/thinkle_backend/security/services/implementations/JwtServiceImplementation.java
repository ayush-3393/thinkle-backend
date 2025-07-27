package com.thinkle_backend.security.services.implementations;

import com.thinkle_backend.security.services.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j

public class JwtServiceImplementation implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400}") // Default 24 hours in seconds
    private long jwtExpirationInSeconds;

    @Value("${jwt.refresh-expiration:604800}") // Default 7 days in seconds
    private long refreshExpirationInSeconds;

    @Value("${jwt.issuer:thinkle-app}")
    private String issuer;

    private SecretKey key;

    @PostConstruct
    public void init() {
        try {
            // Validate secret length (should be at least 256 bits/32 bytes for HS256)
            byte[] keyBytes = Base64.getDecoder().decode(secret);
            if (keyBytes.length < 32) {
                throw new IllegalArgumentException("JWT secret must be at least 32 bytes (256 bits) long");
            }

            this.key = Keys.hmacShaKeyFor(keyBytes);
            log.info("JWT secret key initialized successfully ({} bytes)", keyBytes.length);
            log.info("JWT expiration set to {} seconds", jwtExpirationInSeconds);

        } catch (Exception e) {
            log.error("Failed to initialize JWT secret key", e);
            throw new IllegalStateException("JWT configuration error", e);
        }
    }

    @Override
    public String generateToken(String userEmail) {
        return generateToken(userEmail, new HashMap<>());
    }

    /**
     * Generate JWT token with custom claims
     */
    public String generateToken(String userEmail, Map<String, Object> extraClaims) {
        return buildToken(userEmail, extraClaims, jwtExpirationInSeconds);
    }

    private String buildToken(String userEmail, Map<String, Object> extraClaims, long expirationSeconds) {
        if (!StringUtils.hasText(userEmail)) {
            throw new IllegalArgumentException("User email cannot be null or empty");
        }

        Instant now = Instant.now();
        Instant expiration = now.plus(expirationSeconds, ChronoUnit.SECONDS);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userEmail)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(key)
                .compact();
    }

    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract issued at date from token
     */
    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract issuer from token
     */
    public String extractIssuer(String token) {
        return extractClaim(token, Claims::getIssuer);
    }

    /**
     * Check if token can be refreshed (not expired for too long)
     */
    public boolean canTokenBeRefreshed(String token) {
        try {
            Date expiration = extractExpiration(token);
            // Allow refresh if token expired less than 1 hour ago
            Date refreshCutoff = Date.from(Instant.now().minus(1, ChronoUnit.HOURS));
            return expiration.after(refreshCutoff);
        } catch (Exception e) {
            log.debug("Cannot refresh token: {}", e.getMessage());
            return false;
        }
    }


    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            if (!StringUtils.hasText(token) || userDetails == null) {
                return false;
            }

            String username = extractUserName(token);
            String issuerFromToken = extractIssuer(token);

            return username.equals(userDetails.getUsername())
                    && !isTokenExpired(token)
                    && issuer.equals(issuerFromToken);

        } catch (ExpiredJwtException e) {
            log.debug("Token expired: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate token without user details (for basic token validation)
     */
    public boolean isTokenValid(String token) {
        try {
            if (!StringUtils.hasText(token)) {
                return false;
            }

            Claims claims = extractAllClaims(token);
            String issuerFromToken = claims.getIssuer();

            return !isTokenExpired(token) && issuer.equals(issuerFromToken);

        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract all custom claims from token
     */
    public Map<String, Object> extractAllCustomClaims(String token) {
        Claims claims = extractAllClaims(token);
        Map<String, Object> customClaims = new HashMap<>(claims);

        // Remove standard claims
        customClaims.remove("sub");     // subject
        customClaims.remove("iss");     // issuer
        customClaims.remove("exp");     // expiration
        customClaims.remove("iat");     // issued at
        customClaims.remove("nbf");     // not before
        customClaims.remove("jti");     // JWT ID

        return customClaims;
    }

    /**
     * Get remaining time until token expires (in seconds)
     */
    public long getTokenRemainingTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            long remainingTime = (expiration.getTime() - System.currentTimeMillis()) / 1000;
            return Math.max(0, remainingTime);
        } catch (Exception e) {
            log.debug("Cannot get remaining time: {}", e.getMessage());
            return 0;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // Still return claims even if expired (for refresh token scenarios)
            return e.getClaims();
        }
    }
}
