package com.vip.server.services;

import com.vip.server.domain.Hold;
import com.vip.server.repositories.HoldRepository;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class BalanceServiceCronTest extends AbstractTest {
    @Inject
    private HoldRepository holdRepository;
    @Inject
    private BalanceServiceImpl balanceService;


    @Test
    void autoCloseOutdatedHolds() {
        Hold hold23 = new Hold(
                Hold.HoldStatus.ACTIVE,
                LocalDateTime.now().minusHours(23),
                LocalDateTime.now(),
                "lived active hold",
                "",
                1,
                BigDecimal.TEN
        );
        Hold hold25 = new Hold(
                Hold.HoldStatus.ACTIVE,
                LocalDateTime.now().minusHours(25),
                LocalDateTime.now(),
                "test",
                "",
                1,
                BigDecimal.TEN
        );
        Hold holdCanceled = new Hold(
                Hold.HoldStatus.CANCELED,
                LocalDateTime.now().minusHours(25),
                LocalDateTime.now(),
                "test",
                "",
                1,
                BigDecimal.TEN
        );
        Hold holdDone = new Hold(
                Hold.HoldStatus.DONE,
                LocalDateTime.now().minusHours(25),
                LocalDateTime.now(),
                "test",
                "",
                1,
                BigDecimal.TEN
        );
        Hold expectedHold = holdRepository.save(hold23);
        holdRepository.save(hold25);
        holdRepository.save(holdCanceled);
        holdRepository.save(holdDone);
        assertEquals(4, holdRepository.findAll().size());

        balanceService.closeOutdatedHolds();
        List<Hold> allHolds = holdRepository.findAll();
        assertEquals(4, allHolds.size());
        List<Hold> activeHolds = allHolds.stream()
                .filter(hold -> hold.getHoldStatus() == Hold.HoldStatus.ACTIVE)
                .collect(Collectors.toList());
        assertEquals(1, activeHolds.size());
        assertEquals(expectedHold.getOpeningReason(), activeHolds.get(0).getOpeningReason());
        assertEquals(expectedHold.getId(), activeHolds.get(0).getId());
    }

}
