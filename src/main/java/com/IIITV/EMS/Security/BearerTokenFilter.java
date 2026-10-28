
package com.IIITV.EMS.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.IIITV.EMS.Security.JwtService;
import com.IIITV.EMS.Repository.UserRepository;
import com.IIITV.EMS.Model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import io.jsonwebtoken.JwtException;
import java.util.List;
import java.io.IOException;

@Component
public class BearerTokenFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public BearerTokenFilter(
            JwtService jwtService,
            UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        boolean publicPost =
                "POST".equalsIgnoreCase(method)
                        && (
                        "/api/auth/login".equals(path)
                                || "/api/auth/forgot-password".equals(path)
                                || "/api/auth/reset-password".equals(path)
                );

        if (publicPost) {
            return true;
        }

        return !path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization == null
                || !authorization.startsWith("Bearer ")
                || authorization.substring(7).isBlank()) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    "{\"status\":401,\"error\":\"Bearer token required\"}"
            );
            return;
        }
        try {
            String token = authorization.substring(7);

            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new JwtException("User not found"));

            if (user.getId() == null
                    || user.getStatus() != com.IIITV.EMS.Model.UserStatus.ACTIVE
                    || user.getRole() == null
                    || !user.getRole().name().equals(role)) {
                throw new JwtException("Invalid user or role");
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user.getId(),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            authentication.setDetails(user.getId());

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    "{\"status\":401,\"error\":\"Invalid or expired token\"}"
            );
            return;
        }
        filterChain.doFilter(request, response);

    }
}
