package okadali.tangocache.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import static okadali.tangocache.constants.SystemConstants.*;

@RestController
public class ProxyController {

    private final StringRedisTemplate redisTemplate;

    @Value("${spring.cache.redis.time-to-live}")
    private long timeToLive;

    public ProxyController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @RequestMapping("/**")
    public ResponseEntity<String> proxyRequest(HttpServletRequest request) {
        final String origin = System.getProperty(ORIGIN);
        final HttpMethod method = HttpMethod.valueOf(request.getMethod());
        final String requestURI = request.getRequestURI();

        // path'i al
        final String url = origin + requestURI;

        // path için cache'te içerik var mı bak
        if (redisTemplate.hasKey(url)) {
            // varsa X-cache: HIT olarak header'ı güncelle ve response'u yolla
            String responseData = redisTemplate.opsForValue().get(url);
            return ResponseEntity.ok()
                    .header(X_CACHE, HIT)
                    .body(responseData);
        }

        WebClient webClient = WebClient.builder().baseUrl(url).build();

        String responseBody = webClient
                .method(method)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // yoksa X-cache: MISS olarak header'ı güncelle ve ilgili aderese istek at
        // sonucu redise kaydet ve respone'u yolla
        redisTemplate.opsForValue().set(url, responseBody, Duration.ofMillis(timeToLive));
        return ResponseEntity.ok()
                .header(X_CACHE, MISS)
                .body(responseBody);
    }
}
