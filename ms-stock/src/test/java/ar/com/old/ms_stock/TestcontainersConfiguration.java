package ar.com.old.ms_stock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MySQLContainer;

@TestConfiguration
class TestcontainersConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private static final MySQLContainer<?> mysqlContainer =
			new MySQLContainer<>("mysql:8.0.33")
					.withUsername("test")
					.withPassword("test")
					.withDatabaseName("inventory_stock");

	static {
		mysqlContainer.start();
	}

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		TestPropertyValues.of(
				"spring.datasource.url=" + mysqlContainer.getJdbcUrl(),
				"spring.datasource.username=" + mysqlContainer.getUsername(),
				"spring.datasource.password=" + mysqlContainer.getPassword(),
				"spring.flyway.url=" + mysqlContainer.getJdbcUrl(),
				"spring.flyway.user=" + mysqlContainer.getUsername(),
				"spring.flyway.password=" + mysqlContainer.getPassword()
		).applyTo(applicationContext.getEnvironment());
	}
}
