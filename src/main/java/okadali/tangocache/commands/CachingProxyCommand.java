package okadali.tangocache.commands;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import static okadali.tangocache.constants.SystemConstants.ORIGIN;

@CommandLine.Command(name = "caching-proxy", mixinStandardHelpOptions = true)
@Component
public class CachingProxyCommand implements Runnable {

    @CommandLine.Option(names = "--origin", description = "Origin server URL to proxy")
    String origin;

    @CommandLine.Option(names = "--port", description = "Port number the server will be using")
    String port;

    @CommandLine.Option(names = "--clear-cache", description = "Clear the redis cache and exit")
    boolean clearCache;

    private final StringRedisTemplate redisTemplate;

    public CachingProxyCommand(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run() {
        if (clearCache) {
            redisTemplate.getConnectionFactory().getConnection().flushDb();
            System.out.println("Cache Cleared");
            System.exit(0);
        }

        if (origin != null) {
            System.setProperty(ORIGIN, origin);
        }
    }
}
