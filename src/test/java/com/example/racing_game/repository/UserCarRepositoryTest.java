package com.example.racing_game.repository;

import com.example.racing_game.domain.UserCar;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserCarRepositoryTest {

    @Autowired
    UserCarRepository userCarRepository;

    @Test
    @DisplayName("UserCar 엔티티가 소유자 없이도 기본 정보로 저장")
    void shouldSaveCarSuccessfullyWithoutOwner() {

        UserCar car = new UserCar();
        car.setCarName("Solo_Car_01");

        UserCar savedCar = userCarRepository.save(car);

        assertThat(savedCar.getId()).isNotNull();
        assertThat(savedCar.getCarName()).isEqualTo("Solo_Car_01");
        assertThat(savedCar.getOwner()).isNull();
    }
}