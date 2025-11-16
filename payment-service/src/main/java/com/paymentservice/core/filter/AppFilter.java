package com.paymentservice.core.filter;

import com.paymentservice.core.context.LocalContextHolder;
import com.paymentservice.core.context.RequestContext;
import com.paymentservice.core.enums.ErrorCode;
import com.paymentservice.core.exception.AppException;
import com.paymentservice.kernel.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Set;

@Component
@Order(1)
public class AppFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final HandlerExceptionResolver resolver;

    public AppFilter(JwtUtil jwtUtil,
                     @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtUtil = jwtUtil;
        this.resolver = resolver;
    }

    private static final Set<String> SKIP_PREFIX = Set.of(
            "/auth/login", "/auth/register", "/actuator", "/swagger", "/v3/api-docs"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return SKIP_PREFIX.stream().anyMatch(uri::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        try {
            String auth = req.getHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7).trim();
                var claims = jwtUtil.validateToken(token);
                Integer userId = Integer.valueOf(claims.getBody().getSubject());
                String role  = String.valueOf(claims.getBody().get("role"));

                LocalContextHolder.set(RequestContext.builder()
                        .userId(userId)
                        .role(role)
                        .token(token)
                        .build());
            } else {
                throw new AppException(ErrorCode.AUTH_NO_PERMISSION);
            }

            chain.doFilter(req, res);
        } catch (Exception ex) {
            resolver.resolveException(req, res, null, ex);
        }
        finally {
            LocalContextHolder.clear();
        }
    }
}


