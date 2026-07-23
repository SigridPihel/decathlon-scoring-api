package com.sigridpihel.decathlonscoring.controller;

import com.sigridpihel.decathlonscoring.mapper.DecathlonEventMapper;
import com.sigridpihel.decathlonscoring.model.dto.DecathlonEventDto;
import com.sigridpihel.decathlonscoring.model.enumeration.DecathlonEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8081"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/decathlon-events")
public class DecathlonEventController {

    private final DecathlonEventMapper decathlonEventMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DecathlonEventDto> list() {
        return Arrays.stream(DecathlonEvent.values())
                .map(decathlonEventMapper::toDto)
                .toList();
    }
}
