package com.thepointspup.petpolicyapi.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@Order(1)
public class ApiKeyFilter implements Filter {

    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    @Value("${api.admin-key}")
    private String adminKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (req.getRequestURI().startsWith("/api/") && WRITE_METHODS.contains(req.getMethod())) {
            String provided = req.getHeader("X-API-Key");
            if (provided == null || !provided.equals(adminKey)) {
                res.setStatus(401);
                res.setContentType("application/json");
                res.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Valid X-API-Key header required for write operations\"}");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
