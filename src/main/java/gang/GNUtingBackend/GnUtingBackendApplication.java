package gang.GNUtingBackend;

import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
@EnableJpaAuditing
public class GnUtingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GnUtingBackendApplication.class, args);
	}

//	@Bean
//	public TestData testData(BoardRepository boardRepository, UserRepository userRepository){
//		return new TestData(boardRepository,userRepository);
//	}

	@Bean
	JPAQueryFactory jpaQueryFactory(EntityManager em) {
		return new JPAQueryFactory(em);
	}

}
