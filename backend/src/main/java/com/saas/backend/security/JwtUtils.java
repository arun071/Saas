package com.saas.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for handling JSON Web Tokens (JWT).
 * Provides methods for token generation, validation, and claim extraction.
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Extracts the username (subject) from the given JWT token.
     *
     * @param token The JWT token.
     * @return The username stored in the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the given JWT token.
     *
     * @param token          The JWT token.
     * @param claimsResolver a function to resolve the desired claim from the Claims
     *                       object.
     * @param <T>            The type of the claim.
     * @return The extracted claim value.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT token for the specified user, including organization and
     * schema metadata.
     *
     * @param userDetails    The Spring Security user details.
     * @param organizationId The unique ID of the user's organization.
     * @param schemaName     The database schema name associated with the tenant.
     * @return A signed JWT token string.
     */
    public String generateToken(UserDetails userDetails, String organizationId, String schemaName) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("org_id", organizationId);
        extraClaims.put("schema_name", schemaName);
        return generateToken(extraClaims, userDetails);
    }

    /**
     * Generates a signed JWT token with custom claims.
     *
     * @param extraClaims Additional claims to include in the token payload.
     * @param userDetails The Spring Security user details.
     * @return A signed JWT token string.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates if a JWT token is correct and corresponds to the given user.
     *
     * @param token       The JWT token string.
     * @param userDetails The user details to validate against.
     * @return True if the token is valid and not expired, false otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if the given token has expired.
     *
     * @param token The JWT token string.
     * @return True if expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token The JWT token string.
     * @return The expiration Date object.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Parses all claims from the JWT token using the signing key.
     *
     * @param token The JWT token string.
     * @return The Claims object containing all token data.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Provides the cryptographic key used to sign and verify tokens.
     *
     * @return The HMAC signing key.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
