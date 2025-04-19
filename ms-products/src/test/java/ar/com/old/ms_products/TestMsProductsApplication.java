package ar.com.old.ms_products;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;

public class TestMsProductsApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(MsProductsApplication.class)
				.profiles("integration")
				.initializers(new TestcontainersConfiguration())
				.run(args);
	}
}
