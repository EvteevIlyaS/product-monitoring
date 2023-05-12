package com.ilyaevteev.productmonitoring.repository.auth;

import com.ilyaevteev.productmonitoring.model.auth.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_checkReturnedValue() {
        User user = userRepository.findByUsername("admin").get();

        assertThat(user.getEmail()).isEqualTo("admin@gmail.com");
    }

    @Test
    void updateUserEmail_checkReturnedValue() {
        userRepository.updateUserEmail("admin", "new-email@gmail.com");
        String email = userRepository.findById(1L).get().getEmail();

        assertThat(email).isEqualTo("new-email@gmail.com");
    }

    @Test
    void updateUserPassword_checkReturnedValue() {
        userRepository.updateUserPassword("admin", "new-password");
        String password = userRepository.findById(1L).get().getPassword();

        assertThat(password).isEqualTo("new-password");
    }
}