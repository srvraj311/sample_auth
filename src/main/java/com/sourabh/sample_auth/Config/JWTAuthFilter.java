package com.sourabh.sample_auth.Config;

import com.google.gson.Gson;
import com.sourabh.sample_auth.Excelption.GlobalExceptionHandler;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;


@AllArgsConstructor
@Component
public class JWTAuthFilter extends OncePerRequestFilter {
    JWTTokenUtil jwtTokenUtil;
    CustomuserDetailsService customUserDetailsService;
    GlobalExceptionHandler globalExceptionsHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (request.getRequestURI().contains("/api/")) {
                String authHeader = request.getHeader("Authorization");
                String token = null;
                String username = null;
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                    username = jwtTokenUtil.getUsernameFromToken(token);
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                        if (jwtTokenUtil.validateToken(token, userDetails)) {
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        } else {
                            throw new InsufficientAuthenticationException("Token validation failed");
                        }
                    } else {
                        throw new InsufficientAuthenticationException("Invalid token or token expired");
                    }
                }
            }

            filterChain.doFilter(request, response);
        } catch (InsufficientAuthenticationException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(globalExceptionsHandler.handleRuntimeException(ex).getBody()));
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(globalExceptionsHandler.handleRuntimeException(ex).getBody()));
        }
    }
}