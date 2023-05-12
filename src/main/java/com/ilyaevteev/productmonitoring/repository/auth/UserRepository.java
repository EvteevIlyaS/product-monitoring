package com.ilyaevteev.productmonitoring.repository.auth;

import com.ilyaevteev.productmonitoring.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String name);

    @Modifying
    @Query("update User user set user.email = :email where user.username = :username")
    void updateUserEmail(@Param("username") String username, @Param("email") String email);

    @Modifying
    @Query("update User user set user.password = :password where user.username = :username")
    void updateUserPassword(@Param("username") String username, @Param("password") String password);
}
