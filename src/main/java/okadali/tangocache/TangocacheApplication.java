package okadali.tangocache;

import okadali.tangocache.commands.CommandLineRunnerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TangocacheApplication {

	public static void main(String[] args) {
		new CommandLineRunnerFactory().run(args);
	}

}
