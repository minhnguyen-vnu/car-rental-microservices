package com.iamservice.core.filter;

import com.iamservice.core.constant.ErrorCode;
import com.iamservice.core.constant.exception.AppException;
import com.iamservice.core.context.LocalContextHolder;
import com.iamservice.core.context.RequestContext;
import com.iamservice.core.entity.Account;
import com.iamservice.infrastructure.repository.AccountRepository;
import com.iamservice.kernel.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
    private final AccountRepository repo;
    private final HandlerExceptionResolver resolver;

    public AppFilter(JwtUtil jwtUtil,
                     AccountRepository repo,
                     @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtUtil = jwtUtil;
        this.repo = repo;
        this.resolver = resolver;
    }

    private static final Set<String> SKIP_PREFIX = Set.of(
            "/api/auth/login", "/api/auth/register", "/actuator", "/swagger", "/v3/api-docs"
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

                Account acc = repo.findById(userId).orElseThrow(() -> new AppException(ErrorCode.AUTH_ACCOUNT_NOT_FOUND));

                LocalContextHolder.set(RequestContext.builder()
                        .account(acc)
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
