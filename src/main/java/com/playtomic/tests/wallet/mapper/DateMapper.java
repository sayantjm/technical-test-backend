package com.playtomic.tests.wallet.mapper;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Created by Juanma Perales on 16/7/21
 */
@Component
public class DateMapper {
    public OffsetDateTime asOffsetDateTime(LocalDateTime ts) {
        if (ts != null) {
            return OffsetDateTime.of(ts.getYear(), ts.getMonthValue(),
                    ts.getDayOfMonth(), ts.getHour(), ts.getMinute(),
                    ts.getSecond(), ts.getNano(), ZoneOffset.UTC);
        } else {
            return null;
        }
    }

    public LocalDateTime asTimestamp(OffsetDateTime offsetDateTime) {
        if(offsetDateTime != null) {
            return offsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
        } else {
            return null;
        }
    }

}
