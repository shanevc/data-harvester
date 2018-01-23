package io.data.harvester.service;

import io.data.harvester.domain.HarvesterExecution;
import io.data.harvester.repositories.HarvesterExecutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class HarvesterExecutionServiceImpl implements HarvesterExecutionService {
    @Autowired
    private HarvesterExecutionRepository harvesterExecutionRepository;

    @Override
    public HarvesterExecution save(HarvesterExecution harvesterExecution) {
        harvesterExecution.setLastUpdated(new Date());
        return harvesterExecutionRepository.save(harvesterExecution);
    }

    @Override
    public HarvesterExecution findById(Long id) {
        return harvesterExecutionRepository.findOne(id);
    }

    @Override
    public String getPayload(Long id) {
        String payload = "";
        HarvesterExecution harvesterExecution = harvesterExecutionRepository.findOne(id);
        if (harvesterExecution != null) {
            payload = harvesterExecution.getPayload();
        }
        return payload;
    }
}
