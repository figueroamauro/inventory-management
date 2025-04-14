package ar.com.old.ms_users;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestMsUsersApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(MsUsersApplication.class)
				.profiles("integration")
				.initializers(new TestcontainersConfiguration())
				.run(args);
	}
}
