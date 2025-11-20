package com.example.racing_game.repository;

import com.example.racing_game.domain.PrizePool;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PrizePoolRepositoryTest {

    @Autowired
    PrizePoolRepository prizePoolRepository;

    @Test
    @DisplayName("PrizePool은 ID 1L로 존재하며, 상금액이 정상적으로 업데이트")
    void shouldSaveAndRetrievePrizePoolAmount() {
        PrizePool pool = new PrizePool();
        pool.setAmount(100_000);

        PrizePool savedPool = prizePoolRepository.save(pool);


        assertThat(savedPool.getId()).isEqualTo(1L);

        PrizePool foundPool = prizePoolRepository.findById(1L).orElseThrow();
        assertThat(foundPool.getAmount()).isEqualTo(100_000);

        foundPool.setAmount(foundPool.getAmount() + 50000);
        prizePoolRepository.save(foundPool);

        PrizePool updatedPool = prizePoolRepository.findById(1L).orElseThrow();
        assertThat(updatedPool.getAmount()).isEqualTo(150000);
    }
}