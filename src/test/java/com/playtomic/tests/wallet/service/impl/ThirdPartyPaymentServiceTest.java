package com.playtomic.tests.wallet.service.impl;


import com.playtomic.tests.wallet.repository.WalletRepository;
import com.playtomic.tests.wallet.service.PaymentServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigDecimal;

public class ThirdPartyPaymentServiceTest {

    @Mock
    private WalletRepository mockedRepository;

    ThirdPartyPaymentService s = new ThirdPartyPaymentService(mockedRepository);

    @Test
    public void test_exception() {
        Assertions.assertThrows(PaymentServiceException.class, () -> {
            s.charge(new BigDecimal(5));
        });
    }

    @Test
    public void test_ok() throws PaymentServiceException {
        s.charge(new BigDecimal(15));
    }
}
