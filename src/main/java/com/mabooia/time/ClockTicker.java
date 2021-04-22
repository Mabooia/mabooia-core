package com.mabooia.time;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public final class ClockTicker extends Clock {

    private final Instant startingInstant = Instant.now();
    private final ZoneId defaultZoneId = ZoneId.systemDefault();
    private long count = 0;

    @Override
    public ZoneId getZone() {
        return defaultZoneId;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return Clock.fixed(startingInstant, zone);
    }

    @Override
    public Instant instant() {
        count++;
        return startingInstant.plusSeconds(count);
    }
}
