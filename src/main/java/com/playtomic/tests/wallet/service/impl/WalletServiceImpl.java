package com.playtomic.tests.wallet.service.impl;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.dto.WalletDto;
import com.playtomic.tests.wallet.exception.AmountException;
import com.playtomic.tests.wallet.exception.FoundException;
import com.playtomic.tests.wallet.exception.NotFoundException;
import com.playtomic.tests.wallet.mapper.WalletMapper;
import com.playtomic.tests.wallet.repository.WalletRepository;
import com.playtomic.tests.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created by Juanma Perales on 16/7/21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private static final String AMOUNT_NULL = "Amount to charge is null.";
    private static final String NOT_ENOUGH_AMOUNT = "The wallet has not enough funds.";
    private static final String WALLET_NOT_FOUND = "Wallet NOT FOUND";
    private static final String WALLET_FOUND = "Wallet already exists";

    private final WalletRepository repository;
    private final WalletMapper mapper;

    @Override
    public Optional<WalletDto> findById(Long id) {
        Optional<Wallet> foundWalletOpt = repository.findById(id);
        if (foundWalletOpt.isPresent()) {
            return foundWalletOpt.map(mapper::walletToDto);
        } else {
            throw new NotFoundException(WALLET_NOT_FOUND);
        }
    }

    @Override
    public Optional<WalletDto> create(Long walletId) {
        Optional<Wallet> foundWalletOpt = repository.findById(walletId);
        if (foundWalletOpt.isPresent()) {
            throw new FoundException(WALLET_FOUND);
        } else {
            Wallet newWallet = new Wallet(walletId, 0L, LocalDateTime.now(), null, BigDecimal.ZERO);
            Wallet savedWallet = repository.save(newWallet);
            return Optional.of(mapper.walletToDto(savedWallet));
        }
    }

    @Override
    public Optional<WalletDto> makeCharge(Long walletId, BigDecimal amountToCharge) {
        log.info("Start doing charge to wallet {}", walletId);
        Optional<Wallet> foundWalletOpt = repository.findById(walletId);

        if (foundWalletOpt.isPresent()) {
            Wallet foundWallet = foundWalletOpt.get();
            log.debug("Wallet with id {} found", foundWallet.getId());

            amountValidations(foundWallet, amountToCharge);

            log.debug("Performing charge operation of amount {}", amountToCharge);
            foundWallet.setAmountEur(foundWallet.getAmountEur().subtract(amountToCharge));
            Wallet savedWallet = repository.save(foundWallet);

            log.info("Charge of amount {} completed. Current wallet amount = {}", amountToCharge, savedWallet.getAmountEur());
            return Optional.of(mapper.walletToDto(savedWallet));
        } else {
            log.error(WALLET_NOT_FOUND.concat("ID=").concat(walletId.toString()));
            throw new NotFoundException(WALLET_NOT_FOUND);
        }
    }

    /**
     * Validating amount is not null and the wallet has enough funds to do perform the operation
     * @param foundWallet the wallet where the operation will be performed
     * @param amountToCharge the amount that will be subtracted*
     */
    private void amountValidations(Wallet foundWallet, BigDecimal amountToCharge) {
        if (amountToCharge == null) {
            log.error(AMOUNT_NULL);
            throw new AmountException(AMOUNT_NULL);
        }

        if (foundWallet.getAmountEur().compareTo(amountToCharge) < 0) {
            log.error(NOT_ENOUGH_AMOUNT.concat("Current amount=").concat(foundWallet.getAmountEur().toString()));
            throw new AmountException(NOT_ENOUGH_AMOUNT);
        }
    }
}
