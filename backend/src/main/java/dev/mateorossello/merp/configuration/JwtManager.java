package dev.mateorossello.merp.configuration;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWT;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtManager {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC512(secretKey);
    }

    // Generate a new token with the given information
    public String generateToken(String subject, Long id, String profile, List<String> tasks, Long permissionsVersion) {
        return JWT.create()
            .withSubject(subject)
            .withClaim("id", id)
            .withClaim("profile", profile)
            .withClaim("tasks", tasks)
            .withClaim("permissionsVersion", permissionsVersion)
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
            .sign(getAlgorithm());
    }

    // Clean the prefix from the token
    private String cleanToken(String token) {
        if (token == null) {
            throw new RuntimeException("Token is null.");
        }

        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        return token;
    }

    // Get the decoded token
    private DecodedJWT getDecoded(String token) {
        token = cleanToken(token);
        return JWT.require(getAlgorithm()).build().verify(token);
    }

    // Validate and get the decoded token
    public DecodedJWT validateAndGetDecoded(String token) {
        return getDecoded(token);
    }

    // Validate if the token is valid
    public boolean isValid(String token) {
        try {
            getDecoded(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
