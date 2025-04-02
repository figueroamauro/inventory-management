package ar.com.old.ms_users.repositories;

import static org.junit.jupiter.api.Assertions.*;

import ar.com.old.ms_users.entities.User;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE, schema = "PUBLIC")
@DBRider
@DataSet("users.json")
class UserRepositoryITest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindAllEnabledUsers() {
        //GIVEN
        Pageable pageable = PageRequest.of(0, 10);

        //WHEN
        Page<User> result = userRepository.findAllByEnabledTrue(pageable);

        //THEN
        assertNotNull(result);
        assertEquals(4, result.getTotalElements());

    }
}