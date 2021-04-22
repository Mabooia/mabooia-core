package com.mabooia.time;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class Clocks {

    public static Clock ticker() {
        return new ClockTicker();
    }

    public static Clock travelToDateTime(final LocalDateTime localDateTime, ZoneOffset zoneOffset) {
        return new ClockTimeTravel(localDateTime, zoneOffset);
    }

    public static Clock controlled(final LocalDateTime localDateTime, ZoneOffset zoneOffset) {
        return new ClockTimeControl(localDateTime, zoneOffset);
    }

    private Clocks() {}
}
