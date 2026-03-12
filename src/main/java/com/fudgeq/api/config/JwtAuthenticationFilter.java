package com.fudgeq.api.config;

import com.fudgeq.api.entity.User;
import com.fudgeq.api.enums.UserStatus;
import com.fudgeq.api.service.UserService;
import com.fudgeq.api.utill.JWTTokenGenerator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTTokenGenerator jwtTokenGenerator;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            userEmail = jwtTokenGenerator.extractEmail(jwt);
        } catch (Exception e) {
            sendError(response, 401, "Invalid JWT token");
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if (jwtTokenGenerator.validateToken(jwt)) {

                User user = userService.getUserEntityByEmail(userEmail);

                // User Status Validations
                if (user.getStatus() == UserStatus.REJECTED) {
                    sendError(response, 403, "Your account registration was rejected.");
                    return;
                }

                if (user.getStatus() == UserStatus.PENDING_APPROVAL) {
                    sendError(response, 403, "Your account is pending approval by an admin.");
                    return;
                }

                if (!user.isActive()) {
                    sendError(response, 403, "Your account has been deactivated.");
                    return;
                }

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
                "{\"success\": false, \"status\": %d, \"message\": \"%s\"}",
                status, message
        ));
    }
}
