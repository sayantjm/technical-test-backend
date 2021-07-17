package com.playtomic.tests.wallet.service.impl;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.dto.WalletDto;
import com.playtomic.tests.wallet.exception.AmountException;
import com.playtomic.tests.wallet.mapper.WalletMapper;
import com.playtomic.tests.wallet.repository.WalletRepository;
import com.playtomic.tests.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    private final WalletRepository repository;
    private final WalletMapper mapper;

    public Optional<WalletDto> findById(Long id) {
        Optional<Wallet> walletFound = repository.findById(id);

        return walletFound.map(mapper::walletToDto);
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
        }
        return Optional.empty();
    }

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
