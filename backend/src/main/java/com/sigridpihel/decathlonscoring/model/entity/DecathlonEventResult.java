package com.sigridpihel.decathlonscoring.model.entity;

import com.sigridpihel.decathlonscoring.model.enumeration.DecathlonEvent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
public class DecathlonEventResult {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String athleteName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DecathlonEvent event;

    @Column(nullable = false)
    private BigDecimal performanceValue;

    @Column(nullable = false)
    private LocalDate resultDate;

    @Column(nullable = false)
    private Integer points;
}
