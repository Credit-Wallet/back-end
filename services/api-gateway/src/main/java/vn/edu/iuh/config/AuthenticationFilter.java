package vn.edu.iuh.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import vn.edu.iuh.exception.ErrorCode;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.service.AccountService;

import java.util.List;
import java.util.Set;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {
    private final AccountService accountService;
    private final Set<String> unauthenticatedPaths = Set.of("/eureka/**","/api/v1/auth/login",
            "/api/v1/auth/register");
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Enter authentication filter");
        String path = exchange.getRequest().getPath().value();
        unauthenticatedPaths.forEach(p -> log.info("Unauthenticated path: {}",p));
        System.out.println("Path: " + path);
        if (unauthenticatedPaths.stream().anyMatch(p -> pathMatcher.match(p, path))) {
            return chain.filter(exchange);
        }
        List<String> authHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if(CollectionUtils.isEmpty(authHeaders)){
            return unauthenticated(exchange.getResponse());
        }
        String token = authHeaders.get(0);
        log.info("Token: {}",token);

        return accountService.validateToken(token)
                .flatMap(introspectResponseApiResponse -> {
            if(introspectResponseApiResponse.getResult().getEmail()!=null){
                return chain.filter(exchange);
            }else{
                return unauthenticated(exchange.getResponse());
            }
        }).onErrorResume(
                throwable -> unauthenticated(exchange.getResponse())
                );
    }

    @Override
    public int getOrder() {
        return -1;
    }
    Mono<Void> unauthenticated(ServerHttpResponse response){
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(ErrorCode.UNAUTHENTICATED.getCode())
                .result(ErrorCode.UNAUTHENTICATED.getMessage())
                .build();
        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
