package com.njdealsandclicks.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

@Component
public class DateUtil {
    
    public static final String ZONE_ID = "UTC";

    
    public ZonedDateTime getCurrentDateTime() {
        return ZonedDateTime.now(ZoneId.of(ZONE_ID));
    }
}
