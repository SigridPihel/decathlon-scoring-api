package com.sigridpihel.decathlonscoring.model.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DecathlonEvent {
    RUN_100M(25.4347, 18, 1.81, EventType.TRACK, PerformanceUnit.SECONDS, "100 m"),
    LONG_JUMP(0.14354, 220, 1.4, EventType.FIELD, PerformanceUnit.CENTIMETERS, "Long jump"),
    SHOT_PUT(51.39, 1.5, 1.05, EventType.FIELD, PerformanceUnit.METERS, "Shot put"),
    HIGH_JUMP(0.8465, 75, 1.42, EventType.FIELD, PerformanceUnit.CENTIMETERS, "High jump"),
    RUN_400M(1.53775, 82, 1.81, EventType.TRACK, PerformanceUnit.SECONDS, "400 m"),

    HURDLES_110M(5.74352, 28.5, 1.92, EventType.TRACK, PerformanceUnit.SECONDS, "110 hurdles"),
    DISCUS_THROW(12.91, 4, 1.1, EventType.FIELD, PerformanceUnit.METERS, "Discus throw"),
    POLE_VAULT(0.2797, 100, 1.35, EventType.FIELD, PerformanceUnit.CENTIMETERS, "Pole vault"),
    JAVELIN_THROW(10.14, 7, 1.08, EventType.FIELD, PerformanceUnit.METERS, "Javelin throw"),
    RUN_1500M(0.03768, 480, 1.85, EventType.TRACK, PerformanceUnit.SECONDS, "1500 m");

    private final double aConstant;
    private final double bConstant;
    private final double cConstant;
    private final EventType type;
    private final PerformanceUnit unit;
    private final String displayName;
}
