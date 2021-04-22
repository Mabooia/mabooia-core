package com.mabooia.time;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

public final class ClockTimeControl extends Clock {

    private final LocalDateTime localDateTime;
    private final ZoneOffset zoneOffset;
    private Instant instant;

    public ClockTimeControl(final LocalDateTime localDateTime, final ZoneOffset zoneOffset){
        this.zoneOffset = zoneOffset;
        this.localDateTime = localDateTime;
        this.instant = localDateTime.toInstant(zoneOffset);
    }

    @Override
    public ZoneId getZone() {
        return zoneOffset;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new ClockTimeControl(localDateTime, (ZoneOffset) zone);
    }

    @Override
    public Instant instant() {
        return instant;
    }

    public void plus(final TemporalAmount temporalAmount) {
        instant = instant.plus(temporalAmount);
    }

    public void plus(final long amount, final TemporalUnit temporalUnit) {
        instant = instant.plus(amount, temporalUnit);
    }

    public void plusSeconds(final long secondsToAdd) {
        instant = instant.plusSeconds(secondsToAdd);
    }
}
