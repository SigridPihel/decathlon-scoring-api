package com.sigridpihel.decathlonscoring.model.dto;

import com.sigridpihel.decathlonscoring.model.enumeration.DecathlonEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record DecathlonEventResponseDto(UUID id,
                                        String athleteName,
                                        DecathlonEvent event,
                                        BigDecimal performanceValue,
                                        LocalDate resultDate,
                                        Integer points) {}
