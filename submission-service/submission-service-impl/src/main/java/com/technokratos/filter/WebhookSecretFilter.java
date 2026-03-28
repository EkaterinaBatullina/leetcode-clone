package com.technokratos.filter;

import com.technokratos.config.properties.Judge0Properties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
@Slf4j
public class WebhookSecretFilter extends OncePerRequestFilter {

    private final Judge0Properties properties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("Incoming request: {}", request.getRequestURI());
        log.debug("Header: {}", request.getHeader("X-Webhook-Secret"));
        String actualSecret = request.getHeader("X-Webhook-Secret");
        if (!properties.getSecret().equals(actualSecret)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("Invalid webhook secret");
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().equals("/api/v1/judge0/webhook");
    }
}
