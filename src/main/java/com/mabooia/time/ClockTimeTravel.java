package com.mabooia.time;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public final class ClockTimeTravel extends Clock {

    private final Instant instant;
    private final LocalDateTime localDateTime;
    private final ZoneOffset zoneOffset;
    private final long creationTimestamp;

    public ClockTimeTravel(final LocalDateTime localDateTime, final ZoneOffset zoneOffset){
        this.zoneOffset = zoneOffset;
        this.localDateTime = localDateTime;
        this.instant = localDateTime.toInstant(zoneOffset);
        this.creationTimestamp = Instant.now().toEpochMilli();
    }

    @Override
    public ZoneId getZone() {
        return zoneOffset;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new ClockTimeTravel(localDateTime, (ZoneOffset) zone);
    }

    @Override
    public Instant instant() {
        final long diff = Instant.now().toEpochMilli() - creationTimestamp;
        return instant.plusMillis(diff);
    }
}
