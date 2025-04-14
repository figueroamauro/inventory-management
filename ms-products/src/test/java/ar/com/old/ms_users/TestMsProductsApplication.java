package ar.com.old.ms_users;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestMsProductsApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(MsProductsApplication.class)
				.profiles("integration")
				.initializers(new TestcontainersConfiguration())
				.run(args);
	}
}
