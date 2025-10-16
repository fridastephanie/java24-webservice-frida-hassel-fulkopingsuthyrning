package se.gritacademy.fulkoping_rental.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

    @Value("${app.api-key.user}")
    private String userKey;

    @Value("${app.api-key.admin}")
    private String adminKey;

    /**
     * Returns the role associated with the given API key.
     * Returns null if the key is invalid or missing.
     */
    public String getRoleFromApiKey(String apiKey) {
        if (apiKey == null) return null;
        if (apiKey.equals(adminKey)) return "ADMIN";
        else if (apiKey.equals(userKey)) return "USER";
        return null;
    }
}