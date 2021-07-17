package com.playtomic.tests.wallet.service.impl;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.dto.WalletDto;
import com.playtomic.tests.wallet.exception.AmountException;
import com.playtomic.tests.wallet.exception.NotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

/**
 * Created by Juanma Perales on 16/7/21
 */
@SpringBootTest
@ActiveProfiles("test")
class WalletServiceTest {
    private static final LocalDateTime CURRENT_TEST_TIME = LocalDateTime.of(2021,7,16,15, 0);
    private static final Long WALLET_ID = 2021L;
    private static final String AMOUNT_NULL = "Amount to charge is null.";
    private static final String NOT_ENOUGH_AMOUNT = "The wallet has not enough funds.";
    private static final String WALLET_NOT_FOUND = "Wallet NOT FOUND";

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
    @DisplayName("Query a Wallet By ID")
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

    @Test
    @DisplayName("Query a Wallet By ID throws Not Found Exception")
    void queryWalletByIdNotFound() {
        // Given a wallet with ID 2021. (Setup our mock repository)
        doReturn(Optional.empty()).when(repository).findById(WALLET_ID);

        // When the Wallet is queried Not Found Exception is thrown
        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.findById(WALLET_ID));

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(WALLET_NOT_FOUND));
    }

    @Test
    @DisplayName("Create a new Wallet")
    void createWallet() {
        // Given an ID 2021
        Wallet walletCreated = new Wallet(WALLET_ID, 1L, CURRENT_TEST_TIME, null, BigDecimal.ZERO);
        doReturn(Optional.empty()).when(repository).findById(WALLET_ID);
        doReturn(walletCreated).when(repository).save(any());

        // When create action is requested
        Optional<WalletDto> createdWallet = service.create();

        // Then wallet 2021 has amount of 0
        assertNotNull(createdWallet, "Wallet was null");
        assertTrue(createdWallet.isPresent(), "Wallet was not found");
        assertEquals(BigDecimal.ZERO, createdWallet.get().getAmountEur(), "The wallet has NOT amount of 0");
    }

    @Test
    @DisplayName("Subtract an amount from the wallet")
    void subtractAmountFromWallet() {
        // Given a wallet with ID 2021 and amount of 10
        Wallet wallet = new Wallet(WALLET_ID, 1L, CURRENT_TEST_TIME, null, BigDecimal.TEN);
        Wallet walletUpdated = new Wallet(WALLET_ID, 1L, CURRENT_TEST_TIME, null, new BigDecimal(6L));
        doReturn(Optional.of(wallet)).when(repository).findById(WALLET_ID);
        doReturn(walletUpdated).when(repository).save(any());

        // When subtraction action with amount of 4 to the wallet
        Optional<WalletDto> returnedWallet = service.makeCharge(WALLET_ID, new BigDecimal(4L));

        // Then wallet 2021 has amount of 6
        assertNotNull(returnedWallet, "Wallet was null");
        assertTrue(returnedWallet.isPresent(), "Wallet was not found");
        assertEquals(new BigDecimal(6L), returnedWallet.get().getAmountEur(), "The wallet has NOT amount of 6");
    }

    @Test
    @DisplayName("Subtract an amount from the wallet without enough funds throws a exception.")
    void subtractingAmountFromWalletNotEnoughException() {
        // Given a wallet with ID 2021 and amount of 1
        Wallet wallet = new Wallet(WALLET_ID, 1L, CURRENT_TEST_TIME, null, BigDecimal.ONE);
        doReturn(Optional.of(wallet)).when(repository).findById(WALLET_ID);

        // When subtraction action with amount of 4 to the wallet then AmountException is thrown
        BigDecimal value = new BigDecimal(4L);
        Exception exception = assertThrows(AmountException.class, () -> service.makeCharge(WALLET_ID, value));

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(NOT_ENOUGH_AMOUNT));
    }

    @Test
    @DisplayName("Subtract invalid amount from the wallet throws a exception")
    void subtractInvalidAmountFromWalletException() {
        // Given a wallet with ID 2021 and amount of 1
        Wallet wallet = new Wallet(WALLET_ID, 1L, CURRENT_TEST_TIME, null, BigDecimal.ONE);
        doReturn(Optional.of(wallet)).when(repository).findById(WALLET_ID);

        // When subtraction action with null amount to the wallet then AmountException is thrown
        AmountException exception = assertThrows(AmountException.class, () -> service.makeCharge(WALLET_ID, null));

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(AMOUNT_NULL));
    }

    @Test
    @DisplayName("Subtract amount from not existing wallet throws a Not Found Exception")
    void subtractFromNonExistingWalletException() {
        doReturn(Optional.empty()).when(repository).findById(WALLET_ID);

        // When subtraction action with null amount to the wallet then AmountException is thrown
        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.makeCharge(WALLET_ID, BigDecimal.ONE));

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(WALLET_NOT_FOUND));
    }
}
