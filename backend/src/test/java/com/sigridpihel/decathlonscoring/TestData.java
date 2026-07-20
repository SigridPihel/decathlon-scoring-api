package com.sigridpihel.decathlonscoring;

import com.sigridpihel.decathlonscoring.model.dto.DecathlonEventRequestDto;
import com.sigridpihel.decathlonscoring.model.dto.DecathlonEventResponseDto;
import com.sigridpihel.decathlonscoring.model.entity.DecathlonEventResult;
import com.sigridpihel.decathlonscoring.model.enumeration.DecathlonEvent;
import com.sigridpihel.decathlonscoring.model.enumeration.PerformanceUnit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class TestData {

    public static final String athleteName = "Haaland";
    public static final DecathlonEvent event = DecathlonEvent.RUN_100M;
    public static final BigDecimal performanceValue = new BigDecimal("10.83");
    public static final LocalDate resultDate = LocalDate.of(2026, 7, 15);
    public static final UUID decathlonEventResultId = UUID.randomUUID();
    public static final Integer points = 899;
    public static PerformanceUnit unit = PerformanceUnit.SECONDS;

    public static DecathlonEventResponseDto createDecathlonEventResponseDto() {
        return new DecathlonEventResponseDto(
                decathlonEventResultId,
                athleteName,
                event,
                performanceValue,
                resultDate,
                points,
                unit);
    }

    public static DecathlonEventResult createDecathlonEventResult() {
        DecathlonEventResult resultEntity = new DecathlonEventResult();
        resultEntity.setId(decathlonEventResultId);
        resultEntity.setAthleteName(athleteName);
        resultEntity.setEvent(event);
        resultEntity.setPerformanceValue(performanceValue);
        resultEntity.setResultDate(resultDate);
        return resultEntity;
    }

    public static DecathlonEventRequestDto createDecathlonEventRequestDto() {
        return new DecathlonEventRequestDto(athleteName, event, performanceValue, resultDate);
    }
}
