package org.example.devac.config;

import org.example.devac.utils.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {




    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Rutas públicas adicionales (GET únicamente)
        String path = request.getRequestURI();
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            if (path.matches("/mascota/\\d+") || path.equals("/mascota/findAllLost")) {
                return true; // Permitir acceso sin JWT
            }
        }

        Cookie[] cookies = request.getCookies();
        Long userId = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (JwtUtils.validateToken(token)) {
                        userId = JwtUtils.extractUserId(token);
                        request.setAttribute("USER_ID", userId); // para usar en controllers
                    } else {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        return false;
                    }
                    break;
                }
            }
        }

        if (userId == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        return true;
    }
}
