package com.sigridpihel.decathlonscoring.repository;

import com.sigridpihel.decathlonscoring.model.entity.DecathlonEventResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DecathlonEventResultRepository extends JpaRepository<DecathlonEventResult, UUID> {
}
