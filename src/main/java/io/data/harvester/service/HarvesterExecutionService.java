package io.data.harvester.service;

import io.data.harvester.domain.HarvesterExecution;

public interface HarvesterExecutionService {
    HarvesterExecution save(HarvesterExecution harvesterExecution);
    HarvesterExecution findById(Long id);
    String getPayload(Long id);
}
