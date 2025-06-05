package okadali.tangocache.commands;

import org.springframework.stereotype.Component;

import static picocli.CommandLine.*;

@Command(name = "caching-proxy")
@Component
public class CachingProxyCommand implements Runnable {

    @Option(names = "--port", description = "Port to run the proxy server on")
    Integer port;

    @Option(names = "--origin", description = "Origin server URL to proxy")
    String origin;




    @Override
    public void run() {

        if(port != null) {
            System.setProperty("server.port", port.toString());
        }

        if(origin != null) {
            System.setProperty("origin", origin);
        }

    }
}
