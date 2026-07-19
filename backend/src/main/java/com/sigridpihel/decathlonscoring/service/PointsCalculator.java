package com.sigridpihel.decathlonscoring.service;

import com.sigridpihel.decathlonscoring.model.enumeration.DecathlonEvent;
import com.sigridpihel.decathlonscoring.model.enumeration.EventType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
public class PointsCalculator {

    public Integer calculateEventPoints(DecathlonEvent decathlonEvent, BigDecimal performanceValue) {
        double a = decathlonEvent.getA();
        double b = decathlonEvent.getB();
        double c = decathlonEvent.getC();
        double performanceAsDouble = performanceValue.doubleValue();

        EventType event = decathlonEvent.getType();

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
