package se.gritacademy.fulkoping_rental.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final ApiKeyService apiKeyService;

    public ApiKeyAuthFilter(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    /**
     * Intercepts each HTTP request to check for a valid API key,
     * validates the key, sets the authentication context,
     * and continues the filter chain.
     * Skips API key check for Swagger UI and OpenAPI docs.
     * Sends 401 if key is missing or invalid.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        if (isSwaggerPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String apiKey = getApiKey(request);
        if (apiKey == null) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing API key");
            return;
        }
        String role;
        try {
            role = validateApiKey(apiKey);
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid API key");
            return;
        }
        setAuthentication(apiKey, role);
        filterChain.doFilter(request, response);
    }

    /**
     * Checks if the request URI is for Swagger UI or OpenAPI documentation.
     * Returns true if the API key check should be skipped for this path.
     */
    private boolean isSwaggerPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
    }

    /**
     * Extracts the API key from the request headers.
     */
    private String getApiKey(HttpServletRequest request) {
        String apiKey = request.getHeader("X-API-KEY");
        return (apiKey != null && !apiKey.isEmpty()) ? apiKey : null;
    }

    /**
     * Validates the API key and returns the corresponding role.
     * Throws exception if the key is invalid.
     */
    private String validateApiKey(String apiKey) {
        String role = apiKeyService.getRoleFromApiKey(apiKey);
        if (role == null) {
            throw new RuntimeException("Invalid API key");
        }
        return role;
    }

    /**
     * Sets the authentication object in the SecurityContext.
     */
    private void setAuthentication(String apiKey, String role) {
        var auth = new UsernamePasswordAuthenticationToken(
                apiKey, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * Sends an error response with the given status and message.
     */
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.getWriter().write(message);
    }
}