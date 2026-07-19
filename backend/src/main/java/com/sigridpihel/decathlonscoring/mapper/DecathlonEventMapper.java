package com.sigridpihel.decathlonscoring.mapper;

import com.sigridpihel.decathlonscoring.model.dto.DecathlonEventRequestDto;
import com.sigridpihel.decathlonscoring.model.dto.DecathlonEventResponseDto;
import com.sigridpihel.decathlonscoring.model.entity.DecathlonEventResult;
import org.springframework.stereotype.Component;

@Component
public class DecathlonEventMapper {

    public DecathlonEventResult toEntity(DecathlonEventRequestDto dto) {
        DecathlonEventResult result = new DecathlonEventResult();
        result.setAthleteName(dto.athleteName());
        result.setEvent(dto.event());
        result.setPerformanceValue(dto.performanceValue());
        result.setResultDate(dto.resultDate());
        return result;
    }

    public DecathlonEventResponseDto toDto(DecathlonEventResult entity) {
        return new DecathlonEventResponseDto(
                entity.getId(),
                entity.getAthleteName(),
                entity.getEvent(),
                entity.getPerformanceValue(),
                entity.getResultDate(),
                entity.getPoints(),
                entity.getEvent().getUnit());
    }
}
