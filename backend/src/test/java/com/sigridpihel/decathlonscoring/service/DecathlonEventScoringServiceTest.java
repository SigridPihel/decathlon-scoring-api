package com.sigridpihel.decathlonscoring.service;

import com.sigridpihel.decathlonscoring.TestData;
import com.sigridpihel.decathlonscoring.mapper.DecathlonEventMapper;
import com.sigridpihel.decathlonscoring.model.dto.DecathlonEventRequestDto;
import com.sigridpihel.decathlonscoring.model.dto.DecathlonEventResponseDto;
import com.sigridpihel.decathlonscoring.model.entity.DecathlonEventResult;
import com.sigridpihel.decathlonscoring.repository.DecathlonEventResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;

import static com.sigridpihel.decathlonscoring.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DecathlonEventScoringServiceTest {

    @Mock
    private DecathlonEventResultRepository decathlonEventResultRepository;

    @Mock
    private DecathlonEventMapper decathlonEventMapper;

    @Mock
    private PointsCalculator calculator;

    @InjectMocks
    DecathlonEventScoringService decathlonEventScoringService;

    @Test
    void shouldReturnDecathlonEventResponseDto_whenResultIsSavedSuccessfully() {
        DecathlonEventRequestDto request = createDecathlonEventRequestDto();
        DecathlonEventResult resultEntity = createDecathlonEventResult();
        Integer points = TestData.points;
        DecathlonEventResponseDto responseDto = createDecathlonEventResponseDto();

        when(decathlonEventMapper.toEntity(request)).thenReturn(resultEntity);
        when(calculator.calculateEventPoints(resultEntity.getEvent(), resultEntity.getPerformanceValue())).thenReturn(points);
        when(decathlonEventResultRepository.save(resultEntity)).thenReturn(resultEntity);
        when(decathlonEventMapper.toDto(resultEntity)).thenReturn(responseDto);

        DecathlonEventResponseDto result = decathlonEventScoringService.create(request);

        assertEquals(responseDto, result);
        assertEquals(points, resultEntity.getPoints());
        verify(decathlonEventMapper).toEntity(request);
        verify(decathlonEventResultRepository).save(resultEntity);
        verify(decathlonEventMapper).toDto(resultEntity);
    }

    @Test
    void shouldThrowException_whenSavingToDatabaseFails() {
        DecathlonEventRequestDto request = createDecathlonEventRequestDto();
        DecathlonEventResult resultEntity = createDecathlonEventResult();
        Integer points = TestData.points;

        when(decathlonEventMapper.toEntity(request)).thenReturn(resultEntity);
        when(calculator.calculateEventPoints(resultEntity.getEvent(), resultEntity.getPerformanceValue())).thenReturn(points);
        when(decathlonEventResultRepository.save(resultEntity)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> decathlonEventScoringService.create(request)).isInstanceOf(DataIntegrityViolationException.class);

        verify(decathlonEventMapper).toEntity(request);
        verify(decathlonEventResultRepository).save(resultEntity);
        verify(decathlonEventMapper, never()).toDto(any());
    }

    @Test
    void shouldReturnListOfDecathlonEventResponseDto_whenResultsExist() {
        List<DecathlonEventResult>  listOfDecathlonEventResult = createListOfDecathlonEventResult(2);
        List<DecathlonEventResponseDto> listOfDecathlonEventResponseDto = new ArrayList<>();

        when(decathlonEventResultRepository.findAll()).thenReturn(listOfDecathlonEventResult);
        for (DecathlonEventResult decathlonEventResult : listOfDecathlonEventResult) {
            DecathlonEventResponseDto decathlonEventResponseDto = createDecathlonEventResponseDto(decathlonEventResult.getAthleteName());
            listOfDecathlonEventResponseDto.add(decathlonEventResponseDto);
            when(decathlonEventMapper.toDto(decathlonEventResult)).thenReturn(decathlonEventResponseDto);
        }

        List<DecathlonEventResponseDto> result = decathlonEventScoringService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(listOfDecathlonEventResponseDto);
        verify(decathlonEventResultRepository).findAll();
        verify(decathlonEventMapper, times(2)).toDto(any());
    }

    @Test
    void shouldReturnEmptyList_whenNoResultsExist() {
        List<DecathlonEventResult>  listOfDecathlonEventResult = createListOfDecathlonEventResult(0);
        List<DecathlonEventResponseDto> listOfDecathlonEventResponseDto = new ArrayList<>();

        when(decathlonEventResultRepository.findAll()).thenReturn(listOfDecathlonEventResult);
        for (DecathlonEventResult decathlonEventResult : listOfDecathlonEventResult) {
            DecathlonEventResponseDto decathlonEventResponseDto = createDecathlonEventResponseDto(decathlonEventResult.getAthleteName());
            listOfDecathlonEventResponseDto.add(decathlonEventResponseDto);
            when(decathlonEventMapper.toDto(decathlonEventResult)).thenReturn(decathlonEventResponseDto);
        }

        List<DecathlonEventResponseDto> result = decathlonEventScoringService.findAll();

        assertThat(result).hasSize(0);
        assertThat(result).isEqualTo(listOfDecathlonEventResponseDto);
        verify(decathlonEventResultRepository).findAll();
        verify(decathlonEventMapper, times(0)).toDto(any());
    }
}
