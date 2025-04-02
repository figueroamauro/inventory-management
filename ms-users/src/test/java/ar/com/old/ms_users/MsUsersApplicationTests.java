package ar.com.old.ms_users;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class MsUsersApplicationTests {

	@Test
	void contextLoads() {
	}

}
