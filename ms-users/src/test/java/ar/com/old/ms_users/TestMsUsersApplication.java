package ar.com.old.ms_users;

import org.springframework.boot.SpringApplication;

public class TestMsUsersApplication {

	public static void main(String[] args) {
		SpringApplication.from(MsUsersApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
