package com.sigridpihel.decathlonscoring.service;

import com.sigridpihel.decathlonscoring.mapper.DecathlonEventMapper;
import com.sigridpihel.decathlonscoring.model.dto.DecathlonEventRequestDto;
import com.sigridpihel.decathlonscoring.model.dto.DecathlonEventResponseDto;
import com.sigridpihel.decathlonscoring.model.entity.DecathlonEventResult;
import com.sigridpihel.decathlonscoring.repository.DecathlonEventResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class DecathlonEventScoringService {

    private final DecathlonEventResultRepository decathlonEventResultRepository;
    private final DecathlonEventMapper decathlonEventMapper;
    private final PointsCalculator calculator;

    public DecathlonEventResponseDto create(DecathlonEventRequestDto request) {
        DecathlonEventResult resultEntity = decathlonEventMapper.toEntity(request);
        Integer points = calculator.calculateEventPoints(resultEntity.getEvent(), resultEntity.getPerformanceValue());
        resultEntity.setPoints(points);
        resultEntity = decathlonEventResultRepository.save(resultEntity);
        log.info("{} got {} points in {}", resultEntity.getAthleteName(), points, resultEntity.getEvent());
        return decathlonEventMapper.toDto(resultEntity);
    }

    public List<DecathlonEventResponseDto> findAll() {
        return decathlonEventResultRepository.findAll().stream().map(decathlonEventMapper::toDto).toList();
    }
}
