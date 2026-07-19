package com.sigridpihel.decathlonscoring.model.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DecathlonEvent {
    RUN_100M(25.4347, 18, 1.81, EventType.TRACK),
    LONG_JUMP(0.14354, 220, 1.4, EventType.FIELD),
    SHOT_PUT(51.39, 1.5, 1.05, EventType.FIELD),
    HIGH_JUMP(0.8465, 75, 1.42, EventType.FIELD),
    RUN_400M(1.53775, 82, 1.81, EventType.TRACK),

    HURDLES_110M(5.74352, 28.5, 1.92, EventType.TRACK),
    DISCUS_THROW(12.91, 4, 1.1, EventType.FIELD),
    POLE_VAULT(0.2797, 100, 1.35, EventType.FIELD),
    JAVELIN_THROW(10.14, 7, 1.08, EventType.FIELD),
    RUN_1500M(0.03768, 480, 1.85, EventType.TRACK);

    private final double a_constant;
    private final double b_constant;
    private final double c_constant;
    private final EventType type;
}
