package ar.com.old.ms_products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsProductsApplication {
	public static void main(String[] args) {
		SpringApplication.run(MsProductsApplication.class, args);
	}

}
