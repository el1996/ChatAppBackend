package chatApp.entities;

import chatApp.repository.UserRepository;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class UserTest {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TestEntityManager testEntityManager;

    @AfterEach
    @BeforeEach
    public void deleteAllTables(){
        userRepo.deleteAll();
    }

    @Test
    void setId() {
    }

    @Test
    void setName() {
    }

    @Test
    void setEmail() {
    }

    @Test
    void setPassword() {
    }
}