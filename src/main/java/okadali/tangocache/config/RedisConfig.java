package okadali.tangocache.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, JsonNode> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        final RedisTemplate<String, JsonNode> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        final StringRedisSerializer stringSerializer = new StringRedisSerializer();

        final Jackson2JsonRedisSerializer<JsonNode> jsonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, JsonNode.class);

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

}
