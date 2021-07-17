package com.playtomic.tests.wallet.service.impl;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.exception.NotFoundException;
import com.playtomic.tests.wallet.repository.WalletRepository;
import com.playtomic.tests.wallet.service.PaymentService;
import com.playtomic.tests.wallet.service.PaymentServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;


/**
 * A real implementation would call to a third party's payment service (such as Stripe, Paypal, Redsys...).
 *
 * This is a dummy implementation which throws an error when trying to change less than 10€.
 */
@Service
@RequiredArgsConstructor
public class ThirdPartyPaymentService implements PaymentService {
    private BigDecimal threshold = new BigDecimal(10);

    private final WalletRepository walletRepository;

    @Override
    public void charge(BigDecimal amount) throws PaymentServiceException {
        if (amount.compareTo(threshold) < 0) {
            throw new PaymentServiceException();
        }
    }

    @Override
    public void chargeWalletId(Long walletId, BigDecimal amount) throws PaymentServiceException {
        if (amount.compareTo(threshold) < 0) {
            throw new PaymentServiceException();
        }

        Optional<Wallet> walletOpt = walletRepository.findById(walletId);
        if (walletOpt.isPresent()) {
            Wallet wallet = walletOpt.get();
            wallet.setAmountEur(wallet.getAmountEur().add(amount));
            walletRepository.save(wallet);
        } else {
            throw new NotFoundException("Wallet not found");
        }

    }
}
