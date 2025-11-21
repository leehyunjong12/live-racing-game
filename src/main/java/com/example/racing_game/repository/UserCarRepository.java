package com.example.racing_game.repository;

import com.example.racing_game.domain.User;
import com.example.racing_game.domain.UserCar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCarRepository extends JpaRepository<UserCar, Long> {
    long countByOwner(User owner);
}
