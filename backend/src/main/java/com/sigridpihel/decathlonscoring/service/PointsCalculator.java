package com.sigridpihel.decathlonscoring.service;

import com.sigridpihel.decathlonscoring.model.enumeration.DecathlonEvent;
import com.sigridpihel.decathlonscoring.model.enumeration.EventType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
public class PointsCalculator {

    public Integer calculateEventPoints(DecathlonEvent decathlonEvent, BigDecimal performanceValue) {
        double a_constant = decathlonEvent.getA_constant();
        double b_constant = decathlonEvent.getB_constant();
        double c_constant = decathlonEvent.getC_constant();
        double performanceAsDouble = performanceValue.doubleValue();

        EventType event = decathlonEvent.getType();

        return switch (event) {
            case TRACK -> calculateTrackPoints(a_constant, b_constant, c_constant, performanceAsDouble);
            case FIELD -> calculateFieldPoints(a_constant, b_constant, c_constant, performanceAsDouble);
        };
    }

    private Integer calculateFieldPoints(double a, double b, double c, double p) {
        if (p - b <= 0) {
            return 0;
        }
        // the result is always rounded down to the nearest whole integer
        return (int) Math.floor(a * Math.pow(p - b, c));
    }

    private Integer calculateTrackPoints(double a, double b, double c, double p) {
        if (b - p <= 0) {
            return 0;
        }
        // the result is always rounded down to the nearest whole integer
        return (int) Math.floor(a * Math.pow(b - p, c));
    }
}
