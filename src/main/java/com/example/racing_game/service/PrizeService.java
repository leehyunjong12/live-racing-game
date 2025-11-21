package com.example.racing_game.service;

import com.example.racing_game.domain.PrizePool;
import com.example.racing_game.domain.User;
import com.example.racing_game.domain.UserCar;
import com.example.racing_game.repository.PrizePoolRepository;
import com.example.racing_game.repository.UserCarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrizeService {

    private final PrizePoolRepository prizePoolRepository;
    private final UserCarRepository userCarRepository;

    @Transactional
    public void awardWinnersAndReset(List<String> winnerNames) {
        PrizePool pot = prizePoolRepository.findById(1L).orElseThrow();
        long totalPrize = pot.getAmount();

        boolean adminWins = winnerNames.contains("Admin_Bot");
        boolean onlyAdminWins = adminWins && winnerNames.size() == 1;

        if (onlyAdminWins) {
            System.out.println("🤖 Admin_Bot 독식! 상금(" + totalPrize + "원) 이월.");

        } else {
            if (!winnerNames.isEmpty() && totalPrize > 0) {
                long prizePerWinner = totalPrize / winnerNames.size();

                for (String carName : winnerNames) {
                    if ("Admin_Bot".equals(carName)) {
                        continue;
                    }

                    userCarRepository.findByCarName(carName).ifPresent(car -> {
                        User winner = car.getOwner();
                        winner.setBalance(winner.getBalance() + prizePerWinner);
                    });
                }
            }
            pot.setAmount(0);
        }
        userCarRepository.deleteAll();
    }
}