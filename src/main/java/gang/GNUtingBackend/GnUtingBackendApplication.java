package gang.GNUtingBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
@EnableJpaAuditing
public class GnUtingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GnUtingBackendApplication.class, args);
	}

}
