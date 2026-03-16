package com.saas.backend.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Component for verifying Google ID tokens during the OAuth2 login flow.
 */
@Component
public class GoogleAuthVerifier {

    private final GoogleIdTokenVerifier verifier;

    /**
     * Initializes the verifier with the configured Google Client ID.
     *
     * @param clientId The Google Client ID from application properties.
     */
    public GoogleAuthVerifier(@Value("${google.client.id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    /**
     * Verifies the integrity and authenticity of a Google ID token.
     *
     * @param idTokenString The raw ID token string received from the frontend.
     * @return The payload of the verified token, or null if verification fails.
     */
    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                return idToken.getPayload();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
