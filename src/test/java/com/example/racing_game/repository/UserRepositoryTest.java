package com.example.racing_game.repository;

import com.example.racing_game.domain.User;
import com.example.racing_game.domain.UserCar;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import jakarta.persistence.EntityManager;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserCarRepository userCarRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("User 엔티티와 UserCar 엔티티의 1:N 관계 매핑이 정상 동작해야 한다")
    void shouldSaveAndRetrieveUserWithCars() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("hashed");

        UserCar car1 = new UserCar("Pobi_1", user);
        UserCar car2 = new UserCar("Pobi_2", user);

        user.getRegisteredCars().add(car1);
        user.getRegisteredCars().add(car2);

        User savedUser = userRepository.save(user);
        entityManager.flush();

        User foundUser = userRepository.findByUsername("testuser").orElseThrow();

        assertThat(foundUser.getUsername()).isEqualTo("testuser");

        assertThat(foundUser.getRegisteredCars()).hasSize(2);

        assertThat(foundUser.getRegisteredCars().get(0).getOwner().getUsername())
                .isEqualTo("testuser");
    }

    @Test
    @DisplayName("findByUsername 메서드가 정상 동작")
    void shouldFindUserByUsername() {
        User user = new User();
        user.setUsername("auth_test");
        user.setPassword("1234");
        userRepository.save(user);

        User found = userRepository.findByUsername("auth_test").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("auth_test");
    }
}