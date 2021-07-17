package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.dto.WalletDto;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by Juanma Perales on 16/7/21
 */
public interface WalletService {
    Optional<WalletDto> findById(Long id);

    Optional<WalletDto> makeCharge(Long walletId, BigDecimal bigDecimal);

    Optional<WalletDto> create();

}
