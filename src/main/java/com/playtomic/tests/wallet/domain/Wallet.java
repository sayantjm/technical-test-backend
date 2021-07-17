package com.playtomic.tests.wallet.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by Juanma Perales on 16/7/21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "wallet")
public class Wallet extends BaseEntity {
    public Wallet(Long id, Long version, LocalDateTime createdDate, LocalDateTime lastModifiedDate,
                  BigDecimal amountEur) {
        super(id, version, createdDate, lastModifiedDate);
        this.amountEur = amountEur;
    }

    private BigDecimal amountEur;
}
