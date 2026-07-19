package com.sigridpihel.decathlonscoring.service;

import com.sigridpihel.decathlonscoring.model.enumeration.DecathlonEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointsCalculatorTest {

    private final PointsCalculator calculator = new PointsCalculator();

    @Nested
    @DisplayName("Normal correct values per event verified against the Wikipedia's Decathlon page calculator")
    class CorrectValues {
        @Test
        public void run100m_performance10_83ms_returns899Points() {
            int points = calculator.calculateEventPoints(DecathlonEvent.RUN_100M, new BigDecimal("10.83"));
            assertEquals(899, points);
        }

        @Test
        public void longJump_performance500_8cm_returns384Points() {
            int points = calculator.calculateEventPoints(DecathlonEvent.LONG_JUMP, new BigDecimal("500.8"));
            assertEquals(384, points);
        }

        @Test
        public void shotPut_performance4_29m_returns150Points() {
            int points = calculator.calculateEventPoints(DecathlonEvent.SHOT_PUT, new BigDecimal("4.29"));
            assertEquals(150, points);
        }

        @Test
        public void highJump_performance455_8cm_returns3910Points() {
            int points = calculator.calculateEventPoints(DecathlonEvent.HIGH_JUMP, new BigDecimal("455.8"));
            assertEquals(3910, points);
        }

        @Test
        public void run400m_performance15s_returns3105Points() {
            int points = calculator.calculateEventPoints(DecathlonEvent.RUN_400M, new BigDecimal("15"));
            assertEquals(3105, points);
        }

        @Test
        public void hurdles110m_performance24_01s_returns102Points() {
            int points = calculator.calculateEventPoints(DecathlonEvent.HURDLES_110M, new BigDecimal("24.01"));
            assertEquals(102, points);
        }

        @Test
        public void discusThrow_performance67m_returns1230Points() {
            int points = calculator.calculateEventPoints(DecathlonEvent.DISCUS_THROW, new BigDecimal("67"));
            assertEquals(1230, points);
        }

        @Test
        public void poleVault_performance230_67cm_returns201Points() {
            int points = calculator.calculateEventPoints(DecathlonEvent.POLE_VAULT, new BigDecimal("230.67"));
            assertEquals(201, points);
        }

        @Test
        public void javelinThrow_performance100m_returns1355Points() {
            int points = calculator.calculateEventPoints(DecathlonEvent.JAVELIN_THROW, new BigDecimal("100"));
            assertEquals(1355, points);
        }

        @Test
        public void run1500m_performance210s_returns1186Points() {
            int points = calculator.calculateEventPoints(DecathlonEvent.RUN_1500M, new BigDecimal("210"));
            assertEquals(1186, points);
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {
        @Test
        void run100m_performanceEqualsBaseline18s_returnsZero() {
                int points = calculator.calculateEventPoints(DecathlonEvent.RUN_100M, new BigDecimal("18"));
                assertEquals(0, points);
        }

        @Test
        public void longJump_performanceEqualsBaseline220cm_returnsZero() {
            int points = calculator.calculateEventPoints(DecathlonEvent.LONG_JUMP, new BigDecimal("220"));
            assertEquals(0, points);
        }

        @Test
        void run100m_performanceWorseHigherThanBaseline19s_returnsZero() {
            int points = calculator.calculateEventPoints(DecathlonEvent.RUN_100M, new BigDecimal("19"));
            assertEquals(0, points);
        }

        @Test
        public void longJump_performanceWorseLowerThanBaseline219cm_returnsZero() {
            int points = calculator.calculateEventPoints(DecathlonEvent.LONG_JUMP, new BigDecimal("219"));
            assertEquals(0, points);
        }
    }
}
