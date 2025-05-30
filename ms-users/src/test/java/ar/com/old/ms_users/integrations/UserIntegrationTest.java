package ar.com.old.ms_users.integrations;

import ar.com.old.ms_users.TestcontainersConfiguration;
import ar.com.old.ms_users.dto.UserResponseDTO;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("integration")
@ContextConfiguration(initializers = TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIntegrationTest {
    private static final String REQUEST_BODY = """
            {
            "userName": "mauro",
            "password": "pass1234",
            "email": "email@gmail.com"
            }
            """;

    @LocalServerPort
    private int port;

    @Test
    void shouldCreateUser() {
        //GIVEN
        UserResponseDTO response = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(REQUEST_BODY)

                //WHEN
                .when()
                .post("/api/auth/register")

                //THEN
                .then()
                .statusCode(201)
                .extract().as(UserResponseDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.userName()).isEqualTo("mauro");
    }
}
