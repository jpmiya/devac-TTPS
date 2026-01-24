package org.example.devac.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Filtro CORS global que maneja todas las requests.
 * Lee la configuración desde variables de entorno.
 */
public class CorsFilter implements Filter {

    private String allowedOrigins;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Leer desde variable de entorno o usar default
        allowedOrigins = System.getenv("CORS_ALLOWED_ORIGINS");
        if (allowedOrigins == null || allowedOrigins.trim().isEmpty()) {
            allowedOrigins = "http://localhost:4200";
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String origin = httpRequest.getHeader("Origin");
        
        // Verificar si el origin está permitido
        if (origin != null && isOriginAllowed(origin)) {
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            httpResponse.setHeader("Access-Control-Allow-Headers", "*");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Max-Age", "3600");
        }

        // Manejar preflight requests
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isOriginAllowed(String origin) {
        if (allowedOrigins == null) {
            return false;
        }
        String[] origins = allowedOrigins.split(",");
        for (String allowed : origins) {
            if (allowed.trim().equals(origin)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}
