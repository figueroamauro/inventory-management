package ar.com.old.ms_stock;

import org.springframework.boot.builder.SpringApplicationBuilder;

public class TestMsStockApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(MsStockApplication.class)
				.profiles("integration")
				.initializers(new TestcontainersConfiguration())
				.run(args);
	}
}
