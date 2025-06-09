package okadali.tangocache.services;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import okadali.tangocache.dto.ProxyRedisDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import static okadali.tangocache.constants.SystemConstants.ORIGIN;

@Service
public class ProxyService {
    private final RedisTemplate<String, ProxyRedisDTO> redisTemplate;
    private final WebClient.Builder webClientBuilder;

    @Value("${spring.cache.redis.time-to-live}")
    private long timeToLive;

    public ProxyService(RedisTemplate<String, ProxyRedisDTO> redisTemplate, WebClient.Builder webClientBuilder) {
        this.redisTemplate = redisTemplate;
        this.webClientBuilder = webClientBuilder;
    }

    public ProxyRedisDTO handleProxyRequest(HttpServletRequest request) {
        final String origin = System.getProperty(ORIGIN);
        final HttpMethod method = HttpMethod.valueOf(request.getMethod());
        final String requestURI = request.getRequestURI();

        final String url = origin + requestURI;

        if (redisTemplate.hasKey(url)) {
            final ProxyRedisDTO responseData = redisTemplate.opsForValue().get(url);

            return ProxyRedisDTO.builder()
                    .fromCache(true)
                    .status(responseData.getStatus())
                    .body(responseData.getBody())
                    .build();
        }

        final ProxyRedisDTO proxyRedisDTO = webClientBuilder.build()
                .method(method)
                .uri(url)
                .exchangeToMono(response -> {
                    HttpStatus status = HttpStatus.resolve(response.statusCode().value());
                    return response.bodyToMono(JsonNode.class)
                            .map(body -> {
                                ProxyRedisDTO result = new ProxyRedisDTO();

                                result.setBody(body);
                                result.setStatus(status);
                                result.setFromCache(false);

                                return result;
                            });
                })
                .block();

        // yoksa X-cache: MISS olarak header'ı güncelle ve ilgili aderese istek at
        // sonucu redise kaydet ve respone'u yolla
        redisTemplate.opsForValue().set(url, proxyRedisDTO, Duration.ofMillis(timeToLive));

        return proxyRedisDTO;
    }
}
