package ar.com.old.ms_users;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	private static final MySQLContainer<?> mysqlContainer =
			new MySQLContainer<>("mysql:8.0.33")
					.withUsername("test")
					.withPassword("test")
					.withDatabaseName("bonpland");

	static {
		mysqlContainer.start();
		System.setProperty("spring.datasource.url", mysqlContainer.getJdbcUrl());
		System.setProperty("spring.datasource.username", mysqlContainer.getUsername());
		System.setProperty("spring.datasource.password", mysqlContainer.getPassword());

		System.setProperty("spring.flyway.url", mysqlContainer.getJdbcUrl());
		System.setProperty("spring.flyway.user", mysqlContainer.getUsername());
		System.setProperty("spring.flyway.password", mysqlContainer.getPassword());
	}
}
