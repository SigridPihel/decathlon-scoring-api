package com.sigridpihel.decathlonscoring.controller;

import com.sigridpihel.decathlonscoring.model.dto.DecathlonEventRequestDto;
import com.sigridpihel.decathlonscoring.model.dto.DecathlonEventResponseDto;
import com.sigridpihel.decathlonscoring.service.DecathlonEventScoringService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/decathlon-results")
public class DecathlonEventScoringController {

    private final DecathlonEventScoringService decathlonEventScoringService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DecathlonEventResponseDto create (@Valid @RequestBody DecathlonEventRequestDto request) {
        return decathlonEventScoringService.create(request);
    }
}
