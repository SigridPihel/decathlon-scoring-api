package com.sigridpihel.decathlonscoring.model.dto;

import com.sigridpihel.decathlonscoring.model.enumeration.DecathlonEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DecathlonEventRequestDto(@NotBlank String athleteName,
                                       @NotNull DecathlonEvent event,
                                       @Positive @NotNull BigDecimal performanceValue,
                                       @NotNull @PastOrPresent LocalDate resultDate) {}
