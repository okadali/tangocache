package okadali.tangocache.commands;

import okadali.tangocache.TangocacheApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import picocli.CommandLine;

public class CommandLineRunnerFactory {
    public void run(String[] args) {
        Integer port = extractPortFromArgs(args);

        SpringApplicationBuilder builder = new SpringApplicationBuilder()
                .sources(TangocacheApplication.class);

        if (port != null) {
            builder.properties("server.port=" + port);
        }

        ConfigurableApplicationContext context = builder.run(args);

        CachingProxyCommand command = context.getBean(CachingProxyCommand.class);
        new CommandLine(command).execute(args);
    }

    private Integer extractPortFromArgs(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if ("--port".equals(args[i])) {
                try {
                    return Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid port number: " + args[i + 1]);
                }
            }
        }
        return null;
    }
}
