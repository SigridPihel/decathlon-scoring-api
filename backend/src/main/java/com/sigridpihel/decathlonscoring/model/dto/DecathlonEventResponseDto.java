package com.sigridpihel.decathlonscoring.model.dto;

import com.sigridpihel.decathlonscoring.model.enumeration.DecathlonEvent;
import com.sigridpihel.decathlonscoring.model.enumeration.PerformanceUnit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record DecathlonEventResponseDto(UUID id,
                                        String athleteName,
                                        DecathlonEvent event,
                                        BigDecimal performanceValue,
                                        LocalDate resultDate,
                                        Integer points,
                                        PerformanceUnit unit) {}
