package com.playtomic.tests.wallet.mapper;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.dto.WalletDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Juanma Perales on 16/7/21
 */
@Component
public class WalletMapper {

    @Autowired
    private DateMapper dateMapper;

    public WalletDto walletToDto(Wallet wallet) {
        if ( wallet == null ) {
            return null;
        }
        WalletDto walletDto = new WalletDto();
        walletDto.setId(wallet.getId());
        walletDto.setVersion(wallet.getVersion());
        walletDto.setCreatedDate(dateMapper.asOffsetDateTime(wallet.getCreatedDate()));
        walletDto.setLastModifiedDate(dateMapper.asOffsetDateTime(wallet.getLastModifiedDate()));
        walletDto.setAmountEur(wallet.getAmountEur());

        return walletDto;
    }

    public Wallet dtoToWallet(WalletDto walletDto) {
        if ( walletDto == null ) {
            return null;
        }

        Wallet wallet = new Wallet();
        wallet.setId(walletDto.getId());
        wallet.setVersion(walletDto.getVersion());
        wallet.setCreatedDate(dateMapper.asTimestamp(walletDto.getCreatedDate()));
        wallet.setLastModifiedDate(dateMapper.asTimestamp(walletDto.getLastModifiedDate()));
        wallet.setAmountEur(wallet.getAmountEur());

        return wallet;
    }
}
