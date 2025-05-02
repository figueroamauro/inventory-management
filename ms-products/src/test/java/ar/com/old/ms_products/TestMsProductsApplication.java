package ar.com.old.ms_products;

import org.springframework.boot.builder.SpringApplicationBuilder;

public class TestMsProductsApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(MsProductsApplication.class)
				.profiles("integration")
				.initializers(new TestcontainersConfiguration())
				.run(args);
	}
}
