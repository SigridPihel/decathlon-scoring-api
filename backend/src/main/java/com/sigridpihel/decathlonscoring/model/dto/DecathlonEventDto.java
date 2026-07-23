package com.sigridpihel.decathlonscoring.model.dto;

import com.sigridpihel.decathlonscoring.model.enumeration.PerformanceUnit;

public record DecathlonEventDto (String event,
                                 String displayName,
                                 PerformanceUnit unit) {}
