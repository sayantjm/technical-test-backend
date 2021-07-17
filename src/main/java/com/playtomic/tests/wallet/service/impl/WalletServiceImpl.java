package com.playtomic.tests.wallet.service.impl;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.dto.WalletDto;
import com.playtomic.tests.wallet.mapper.WalletMapper;
import com.playtomic.tests.wallet.repository.WalletRepository;
import com.playtomic.tests.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Juanma Perales on 16/7/21
 */
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository repository;
    private final WalletMapper mapper;

    public Optional<WalletDto> findById(Long id) {
        Optional<Wallet> walletFound = repository.findById(id);

        return walletFound.map(mapper::walletToDto);
    }
}
