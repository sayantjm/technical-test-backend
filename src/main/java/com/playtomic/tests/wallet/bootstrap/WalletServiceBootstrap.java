package com.playtomic.tests.wallet.bootstrap;

import com.playtomic.tests.wallet.domain.Wallet;
import com.playtomic.tests.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Juanma Perales on 17/7/21
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class WalletServiceBootstrap implements CommandLineRunner {

    private final WalletRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            loadInitialData();
        }
    }

    private void loadInitialData() {
        Wallet wallet1 = new Wallet(BigDecimal.valueOf(567.45));
        Wallet wallet2 = new Wallet(BigDecimal.valueOf(100L));
        Wallet wallet3 = new Wallet(BigDecimal.ZERO);
        List<Wallet> wallets = Arrays.asList(wallet1, wallet2, wallet3);

        List<Wallet> initialList = wallets.stream().map(repository::save).collect(Collectors.toList());
        log.info("------ Initial wallets created ---------");
        initialList.stream().filter(Objects::nonNull).forEach(wallet -> log.info("Wallet Id: ".concat(wallet.getId().toString()).concat("-Amount:").concat(wallet.getAmountEur().toString())));
        log.info("---------------------------------------");
    }
}
