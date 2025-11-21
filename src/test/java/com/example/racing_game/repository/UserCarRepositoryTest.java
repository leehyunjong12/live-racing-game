package com.example.racing_game.repository;

import com.example.racing_game.domain.User;
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

    @Autowired
    UserRepository userRepository;


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
    @Test
    @DisplayName("특정 사용자가 소유한 자동차의 개수를 정확히 셈 (countByOwner)")
    void shouldCountCarsByOwner() {
        User user1 = new User();
        user1.setUsername("pobi");
        user1.setPassword("pass");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("crong");
        user2.setPassword("pass");
        userRepository.save(user2);

        userCarRepository.save(new UserCar("pobi_1", user1));
        userCarRepository.save(new UserCar("pobi_2", user1));

        userCarRepository.save(new UserCar("crong_1", user2));

        long count1 = userCarRepository.countByOwner(user1);
        long count2 = userCarRepository.countByOwner(user2);

        assertThat(count1).isEqualTo(2);
        assertThat(count2).isEqualTo(1);
    }
}