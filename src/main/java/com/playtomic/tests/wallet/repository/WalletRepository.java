package com.playtomic.tests.wallet.repository;

import com.playtomic.tests.wallet.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Juanma Perales on 16/7/21
 */
public interface WalletRepository extends JpaRepository<Wallet, Long> {

}
