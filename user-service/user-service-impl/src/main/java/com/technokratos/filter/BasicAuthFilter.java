package com.technokratos.filter;

import com.technokratos.config.property.OAuth2ClientProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class BasicAuthFilter extends OncePerRequestFilter {
    /*
     * Basic Auth для эндпоинтов регистрации и логина.
     *
     * Для остальных запросов используется JWT-аутентификация,
     * поэтому дополнительная проверка client credentials не требуется.
     */
    private final RequestMatcher requestMatcher = new OrRequestMatcher(
            new AntPathRequestMatcher("/api/v1/authentication/login", "POST"),
            new AntPathRequestMatcher("/api/v1/authentication/register", "POST")
    );
    private final OAuth2ClientProperties properties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!requestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) {
            reject(response,"Missing or invalid Authorization header");
            return;
        }
        try {
            String[] values = extractCredentials(header);
            String username = values[0];
            String password = values[1];
            /*
             * На этапе логина/регистрации проверяется доверенное приложение-клиент.
             *
             * ClientId и ClientSecret должны совпасть с настройками
             * зарегистрированного клиента.
             */
            if (properties.getClientId().equals(username) && properties.getClientSecret().equals(password)) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, password,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
                /*
                 * Результат проверки клиента помещается в SecurityContext.
                 *
                 * Spring Security будет считать запрос аутентифицированным
                 * и позволит продолжить обработку.
                 *
                 * Очистка SecurityContext после завершения запроса
                 * выполняется инфраструктурой Spring Security автоматически.
                 */
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                reject(response,"Invalid username or password");
                return;
            }
        } catch (Exception e) {
            reject(response, "Failed to parse Authorization header");
            return;
        }
        filterChain.doFilter(request, response);
    }

    /*
     * Basic Authorization:
     * Base64(clientId:clientSecret)
     */
    private String[] extractCredentials(String header) {
        String base64Credentials = header.substring("Basic ".length());
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded);
        return credentials.split(":", 2);
    }

    private void reject(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                "{\"error\":\"" + message + "\"}"
        );
    }
}