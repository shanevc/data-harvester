package io.data.harvester.controller;

import io.data.harvester.configuration.JmsConfig;
import io.data.harvester.domain.HarvesterExecution;
import io.data.harvester.jms.JmsMessage;
import io.data.harvester.service.HarvesterExecutionService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RestController
public class IndexController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private HarvesterExecutionService harvesterExecutionService;

    @RequestMapping(value = "/harvest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public HarvesterExecution scheduleScrape(@RequestParam(value = "pageNum", required = false) Integer pageNumber) {
        log.info("Sending message to queue...");
        HarvesterExecution harvesterExecution = harvesterExecutionService.save(new HarvesterExecution());
        JmsMessage message = pageNumber == null ? new JmsMessage(harvesterExecution.getId()) : new JmsMessage(harvesterExecution.getId(), pageNumber);
        jmsTemplate.convertAndSend(JmsConfig.HARVESTER_PROCESS_QUEUE, message);
        return harvesterExecution;
    }

    @RequestMapping(value = "/get-status/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public HarvesterExecution getStatus(@PathVariable("id") Long icoDataId) {
        return harvesterExecutionService.findById(icoDataId);
    }

    @RequestMapping(value = "/get-payload/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void getIcoData(@PathVariable("id") Long icoDataId, HttpServletResponse response) {
        try {
            InputStream is = new ByteArrayInputStream(harvesterExecutionService.getPayload(icoDataId).getBytes(StandardCharsets.UTF_8.name()));
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            log.error("Error writing file to output stream. " + ex);
            throw new RuntimeException("IOError writing file to output stream");
        }
    }
}
