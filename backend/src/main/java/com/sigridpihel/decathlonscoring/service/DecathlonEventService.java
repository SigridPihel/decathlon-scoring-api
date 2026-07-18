package com.sigridpihel.decathlonscoring.service;

import com.sigridpihel.decathlonscoring.mapper.DecathlonEventMapper;
import com.sigridpihel.decathlonscoring.model.dto.DecathlonEventRequestDto;
import com.sigridpihel.decathlonscoring.model.dto.DecathlonEventResponseDto;
import com.sigridpihel.decathlonscoring.model.entity.DecathlonEventResult;
import com.sigridpihel.decathlonscoring.model.enumeration.EventType;
import com.sigridpihel.decathlonscoring.repository.DecathlonEventResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecathlonEventService {

    private final DecathlonEventResultRepository decathlonEventResultRepository;
    private final DecathlonEventMapper decathlonEventMapper;

    public DecathlonEventResponseDto create(DecathlonEventRequestDto request) {
        DecathlonEventResult resultEntity = decathlonEventMapper.toEntity(request);
        Integer points = calculateEventPoints(resultEntity);
        resultEntity.setPoints(points);
        resultEntity = decathlonEventResultRepository.save(resultEntity);
        log.info("{} got {} points in {}", resultEntity.getAthleteName(), points, resultEntity.getEvent());
        return decathlonEventMapper.toDto(resultEntity);
    }

    private Integer calculateEventPoints(DecathlonEventResult resultEntity) {
        double a = resultEntity.getEvent().getA();
        double b = resultEntity.getEvent().getB();
        double c = resultEntity.getEvent().getC();
        BigDecimal performance = resultEntity.getPerformanceValue();
        double performanceAsDouble = performance.doubleValue();

        EventType event = resultEntity.getEvent().getType();

        return switch (event) {
            case TRACK -> calculateTrackPoints(a, b, c, performanceAsDouble);
            case FIELD -> calculateFieldPoints(a, b, c, performanceAsDouble);
        };
    }

    private Integer calculateFieldPoints(double a, double b, double c, double p) {
        if (p - b <= 0) {
            return 0;
        }
        return (int) Math.floor(a * Math.pow(p - b, c));
    }

    private Integer calculateTrackPoints(double a, double b, double c, double p) {
        if (b - p <= 0) {
            return 0;
        }
        return (int) Math.floor(a * Math.pow(b - p, c));
    }
}
