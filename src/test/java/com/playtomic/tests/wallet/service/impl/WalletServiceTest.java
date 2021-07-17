package com.playtomic.tests.wallet.service.impl;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.dto.WalletDto;
import com.playtomic.tests.wallet.mapper.DateMapper;
import com.playtomic.tests.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

/**
 * Created by Juanma Perales on 16/7/21
 */
@SpringBootTest
@ActiveProfiles("test")
class WalletServiceTest {
    private static final LocalDateTime CURRENT_TEST_TIME = LocalDateTime.of(2021,7,16,15, 0);
    private static final Long WALLET_ID = 2021L;

    @Autowired
    private WalletServiceImpl service;

    @Autowired
    private DateMapper dateMapper;

    @MockBean
    private WalletRepository repository;

    private WalletDto myWallet;

    @BeforeEach
    public void setup() {

        myWallet = WalletDto.builder()
                .id(2021L)
                .version(1L)
                .createdDate(dateMapper.asOffsetDateTime(CURRENT_TEST_TIME))
                .amountEur(BigDecimal.TEN)
                .build();
    }

    @Test
    @DisplayName("Test Query a Wallet By ID")
    void queryWalletById() {
        // Given a wallet with ID 2021. (Setup our mock repository)
        Wallet wallet = new Wallet(WALLET_ID, 1L, CURRENT_TEST_TIME, null, BigDecimal.TEN);
        doReturn(Optional.of(wallet)).when(repository).findById(WALLET_ID);

        // When the Wallet is queried
        Optional<WalletDto> returnedWallet = service.findById(WALLET_ID);

        // Then the wallet 2021 has amount of 10
        assertNotNull(returnedWallet, "Wallet was null");
        assertTrue(returnedWallet.isPresent(), "Wallet was not found");
        assertEquals(BigDecimal.TEN, returnedWallet.get().getAmountEur(), "The wallet has NOT amount of 10");
        assertEquals(returnedWallet.get(), myWallet, "The wallet returned was not the same as the mock");
    }
}
