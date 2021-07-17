package com.playtomic.tests.wallet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

/**
 * Created by Juanma Perales on 17/7/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AmountDto {
    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("currency")
    private String currency = "EUR";
}
