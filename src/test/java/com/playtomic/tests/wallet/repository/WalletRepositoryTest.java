package com.playtomic.tests.wallet.repository;

import com.playtomic.tests.wallet.domain.Wallet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Juanma Perales on 16/7/21
 */
@SpringBootTest
@ActiveProfiles("test")
public class WalletRepositoryTest {

    @Autowired
    private WalletRepository repository;

    @Test
    @DisplayName("Create Wallet Test ")
    void createWalletTest() {
        Wallet walletCreated = repository.save(newWallet());
        assertNotNull(walletCreated);
    }

    @Test
    @DisplayName("Update Wallet Test ")
    void updateWalletTest() {
        Wallet walletCreated = repository.save(newWallet());
        assertNotNull(walletCreated);
        walletCreated.setAmountEur(new BigDecimal(500L));
        Wallet walletUpdated =  repository.save(walletCreated);
        assertEquals(new BigDecimal(500L), walletUpdated.getAmountEur());
    }

    private Wallet newWallet() {
        return Wallet.builder()
                .amountEur(new BigDecimal(1400L))
                .build();
    }
}
