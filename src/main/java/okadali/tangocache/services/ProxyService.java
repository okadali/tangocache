package okadali.tangocache.services;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import okadali.tangocache.dto.ProxyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import static okadali.tangocache.constants.SystemConstants.*;
import static okadali.tangocache.constants.SystemConstants.MISS;
import static okadali.tangocache.constants.SystemConstants.X_CACHE;

@Service
public class ProxyService {
    private final RedisTemplate<String, JsonNode> redisTemplate;
    private final WebClient.Builder webClientBuilder;

    @Value("${spring.cache.redis.time-to-live}")
    private long timeToLive;

    public ProxyService(RedisTemplate<String, JsonNode> redisTemplate, WebClient.Builder webClientBuilder) {
        this.redisTemplate = redisTemplate;
        this.webClientBuilder = webClientBuilder;
    }

    public ProxyResponse handleProxyRequest(HttpServletRequest request) {
        final String origin = System.getProperty(ORIGIN);
        final HttpMethod method = HttpMethod.valueOf(request.getMethod());
        final String requestURI = request.getRequestURI();

        final String url = origin + requestURI;

        if (redisTemplate.hasKey(url)) {
            final JsonNode responseData = redisTemplate.opsForValue().get(url);

            return ProxyResponse.builder()
                    .fromCache(true)
                    .body(responseData)
                    .build();
        }

        JsonNode responseBody = webClientBuilder.build()
                .method(method)
                .uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        // yoksa X-cache: MISS olarak header'ı güncelle ve ilgili aderese istek at
        // sonucu redise kaydet ve respone'u yolla
        redisTemplate.opsForValue().set(url, responseBody, Duration.ofMillis(timeToLive));
        return ResponseEntity.ok()
                .header(X_CACHE, MISS)
                .body(responseBody);
    }
}
