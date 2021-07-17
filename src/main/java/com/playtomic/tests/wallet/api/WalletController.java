package com.playtomic.tests.wallet.api;

import com.playtomic.tests.wallet.dto.AmountDto;
import com.playtomic.tests.wallet.dto.WalletDto;
import com.playtomic.tests.wallet.exception.NotFoundException;
import com.playtomic.tests.wallet.service.PaymentServiceException;
import com.playtomic.tests.wallet.service.WalletService;
import com.playtomic.tests.wallet.service.impl.ThirdPartyPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {
    private static final String WALLET_NOT_CREATED = "Wallet NOT created";
    private static final String WALLET_NOT_FOUND = "Wallet not found with id:";

    private final WalletService walletService;
    private final ThirdPartyPaymentService thirdPartyPaymentService;

    @RequestMapping("/")
    public void log() {
        log.info("Logging from /");
    }

    @GetMapping(value = "{walletId}")
    public WalletDto getById(@PathVariable Long walletId) {
        log.info("Request for get wallet {}", walletId);
        final Optional<WalletDto> walletDtoOpt = walletService.findById(walletId);

        if (!walletDtoOpt.isPresent()) {
            log.error(WALLET_NOT_FOUND.concat(walletId.toString()));
            throw new NotFoundException(WALLET_NOT_FOUND.concat(walletId.toString()));
        }

        log.info("Request for get wallet {} completed!", walletId);
        return walletDtoOpt.get();
    }

    @PostMapping
    public WalletDto createWallet() {
        log.info("Request for create wallet");
        Optional<WalletDto> walletDtoOpt = walletService.create();

        if (!walletDtoOpt.isPresent()) {
            log.error(WALLET_NOT_CREATED);
            throw new NotFoundException(WALLET_NOT_CREATED);
        }
        log.info("Request for create wallet completed!. New Wallet:{}", walletDtoOpt.get().getId());
        return walletDtoOpt.get();
    }

    @PatchMapping(value = "{walletId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public WalletDto chargeWallet(@PathVariable("walletId") final Long walletId, @RequestBody final AmountDto amount) {
        preValidations(walletId, amount);
        log.info("Request for doing a charge from wallet{} with amount of {}", walletId, amount);

        //TODO here we can do the currency conversion before doing the charge

        final Optional<WalletDto> walletDtoOpt = walletService.makeCharge(walletId, amount.getAmount());

        if (!walletDtoOpt.isPresent()) {
            log.error(WALLET_NOT_FOUND.concat(walletId.toString()));
            throw new NotFoundException(WALLET_NOT_FOUND.concat(walletId.toString()));
        }
        log.info("Request for doing a charge from wallet{} with amount of {} completed.", walletId, amount);
        return walletDtoOpt.get();
    }

    @PatchMapping(value = "/nonblocking/{walletId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<WalletDto> nonBlockingChargeWallet(@PathVariable("walletId") final Long walletId, @RequestBody final AmountDto amount) {
        preValidations(walletId, amount);
        log.info("Request for doing a non-blocking charge from wallet{} with amount of {}", walletId, amount);

        //TODO here we can do the currency conversion before doing the charge

        return CompletableFuture.supplyAsync(() -> {
            final Optional<WalletDto> walletDtoOpt = walletService.makeCharge(walletId, amount.getAmount());

            if (!walletDtoOpt.isPresent()) {
                log.error(WALLET_NOT_FOUND.concat(walletId.toString()));
                throw new NotFoundException(WALLET_NOT_FOUND.concat(walletId.toString()));
            }
            log.info("Request for doing a non-blocking charge to wallet{} with amount of {} completed!", walletId, amount);
            return walletDtoOpt.get();
        });
    }

    @PatchMapping(value = "/recharge/{walletId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<WalletDto> nonBlockingReChargeWallet(@PathVariable("walletId") final Long walletId, @RequestBody final AmountDto amount) {
        preValidations(walletId, amount);
        log.info("Request for doing a re-charge to wallet{} with amount of {}", walletId, amount);

        //TODO here we can do the currency conversion before doing the charge

        return CompletableFuture.supplyAsync(() -> {
            try {
                thirdPartyPaymentService.chargeWalletId(walletId, amount.getAmount());
            } catch (PaymentServiceException e) {
                log.error("The amount is too short.".concat(amount.getAmount().toString()));
                throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "The amount is too short.");
            }

            Optional<WalletDto> walletFound = walletService.findById(walletId);
            if(walletFound.isPresent()) {
                return walletFound.get();
            } else {
                log.error(WALLET_NOT_FOUND.concat(walletId.toString()));
                throw new NotFoundException(WALLET_NOT_FOUND.concat(walletId.toString()));
            }
        });
    }

    private void preValidations(final Long id, final AmountDto amount) {
        if ((null == id) || (id <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wallet ID must be greater than 0.");
        }

        if ((null == amount) || (amount.getAmount().compareTo(BigDecimal.ZERO) < 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be greater than 0.");
        }
    }
}
