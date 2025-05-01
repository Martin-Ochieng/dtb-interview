package com.dtb.msgateway.authentication;

import com.dtb.msgateway.config.Config;
import com.dtb.msgateway.util.logging.Logging;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {
    private final Config config;
    private final Logging logging = new Logging();

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        long startTime = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        // ✅ Skip auth check for public paths like Swagger and auth endpoints
        if (
                path.startsWith("/auth/") ||
                        path.contains("/swagger-ui") ||
                        path.contains("/v3/api-docs") ||
                        path.contains("/swagger-resources")
        ) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String transactionId = request.getId(); // You may change this to match your transaction ID source

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logging.setLogLevel("warn")
                    .setTransactionID(transactionId)
                    .setProcess("JWT Filter")
                    .setRequest(path)
                    .setResponse("Missing or invalid Authorization header")
                    .setResponseMsg("Unauthorized")
                    .setProcessDuration(System.currentTimeMillis() - startTime)
                    .write();

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(config.getAuthConfig().getJwtSecret().getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token); // JWT is valid

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    "user", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );

            logging.setLogLevel("info")
                    .setTransactionID(UUID.randomUUID().toString())
                    .setProcess("JWT Filter")
                    .setRequest(request)
                    .setResponseCode(200)
                    .setResponse("JWT validated")
                    .setResponseMsg("Authorized")
                    .setProcessDuration(System.currentTimeMillis() - startTime)
                    .write();

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        } catch (JwtException e) {
            logging.setLogLevel("warn")
                    .setTransactionID(UUID.randomUUID().toString())
                    .setProcess("JWT Filter")
                    .setRequest(request)
                    .setResponseCode(401)
                    .setResponse("JWT invalid")
                    .setResponseMsg("Unauthorized")
                    .setProcessDuration(System.currentTimeMillis() - startTime)
                    .write();

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
