package com.ilyaevteev.productmonitoring.repository.auth;

import com.ilyaevteev.productmonitoring.model.auth.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    void findByName_checkReturnedValue() {
        Role role = roleRepository.findByName("ROLE_ADMIN").get();

        assertThat(role.getId()).isEqualTo(1L);
    }
}