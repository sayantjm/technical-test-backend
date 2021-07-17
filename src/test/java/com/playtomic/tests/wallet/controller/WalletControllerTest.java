package com.playtomic.tests.wallet.controller;

import com.playtomic.tests.wallet.dto.WalletDto;
import com.playtomic.tests.wallet.mapper.DateMapper;
import com.playtomic.tests.wallet.service.WalletService;
import com.playtomic.tests.wallet.service.impl.ThirdPartyPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Juanma Perales on 17/7/21
 */
@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {
    private static final LocalDateTime CURRENT_TEST_TIME = LocalDateTime.of(2021,7,16,15, 0);
    private static final Long WALLET_ID = 2021L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService mockedService;

    @MockBean
    private ThirdPartyPaymentService mockedThirdPartyService;

    @Autowired
    private DateMapper dateMapper;

    private WalletDto myWallet;

    @BeforeEach
    public void setup() {
        myWallet = WalletDto.builder()
                .id(2021L)
                .version(1L)
                .createdDate(dateMapper.asOffsetDateTime(CURRENT_TEST_TIME))
                .amountEur(BigDecimal.TEN)
                .build();
    }

    @Test
    @DisplayName("GET /wallet/{walletId} --> Query a wallet")
    void testGetWalletSuccess() throws Exception {

        // Setup mocked service
        doReturn(Optional.of(myWallet)).when(mockedService).findById(any());

        // Execute GET request
        mockMvc.perform(get("/wallets/".concat(WALLET_ID.toString())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /wallets --> Create a wallet" )
    void testPostWalletSuccess() throws Exception {

        // Setup mocked service
        doReturn(Optional.of(myWallet)).when(mockedService).create();

        // Execute GET request
        mockMvc.perform(post("/wallets"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /wallets/{walletId} --> Subtract amount from a wallet" )
    void testPatchWalletSuccess() throws Exception {

        // Setup mocked service
        doReturn(Optional.of(myWallet)).when(mockedService).findById(any());
        doReturn(Optional.of(myWallet)).when(mockedService).makeCharge(any(), any());

        MockHttpServletRequestBuilder builder = getMockHttpServletRequestBuilder("/wallets/");

        // Execute GET request
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                .string(getWalletInJson()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("PATCH /wallets/nonblocking/{walletId} --> Non blocking Subtract amount from a wallet" )
    void testPatchNonblockingWalletSuccess() throws Exception {

        // Setup mocked service
        doReturn(Optional.of(myWallet)).when(mockedService).findById(any());
        doReturn(Optional.of(myWallet)).when(mockedService).makeCharge(any(), any());

        MockHttpServletRequestBuilder builder = getMockHttpServletRequestBuilder("/wallets/nonblocking/");

        MvcResult mvcResult = mockMvc.perform(builder)
                .andReturn();

        // Execute GET request
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(getWalletInJson()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("PATCH /wallets/recharge/{walletId} --> Recharge amount to a wallet" )
    void testPatchRechargeWalletSuccess() throws Exception {

        // Setup mocked service
        doReturn(Optional.of(myWallet)).when(mockedService).findById(any());
        doReturn(Optional.of(myWallet)).when(mockedService).makeCharge(any(), any());
        doNothing().when(mockedThirdPartyService).chargeWalletId(any(), any());


        MockHttpServletRequestBuilder builder = getMockHttpServletRequestBuilder("/wallets/recharge/");

        MvcResult mvcResult = mockMvc.perform(builder)
                .andReturn();

        // Execute GET request
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(getWalletInJson()))
                .andDo(MockMvcResultHandlers.print());
    }

    private String getAmountInJson(int amount, String currency) {
        return "{\"amount\":\"" + BigDecimal.valueOf(amount) + "\", \"currency\":\"" + currency + "\"}";
    }

    private String getWalletInJson() {
        return "{\"amountEur\":10,\"id\":2021,\"version\":1,\"createdDate\":\"2021-07-16T15:00:00+0000\",\"lastModifiedDate\":null}";
    }

    private MockHttpServletRequestBuilder getMockHttpServletRequestBuilder(String s) {
        return MockMvcRequestBuilders.patch(s + WALLET_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(getAmountInJson(50, "EUR"));
    }

}
