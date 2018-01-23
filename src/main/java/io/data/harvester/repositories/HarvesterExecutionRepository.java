package io.data.harvester.repositories;

import io.data.harvester.domain.HarvesterExecution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HarvesterExecutionRepository extends JpaRepository<HarvesterExecution, Long> {
}
