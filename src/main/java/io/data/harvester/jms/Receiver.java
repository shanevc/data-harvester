package io.data.harvester.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import io.data.harvester.configuration.JmsConfig;
import io.data.harvester.domain.HarvesterExecution;
import io.data.harvester.models.*;
import io.data.harvester.service.HarvesterExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Receiver {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HarvesterExecutionService harvesterExecutionService;

    @Value("${application.icoBenchBaseUrl}")
    private String icoBenchBaseUrl;

    @Value("${application.icoContext}")
    private String icoContext;

    @Value("${application.pageQueryParam}")
    private String pageQueryParam;

    @Value("${application.xpath.icoItems}")
    private String xpathIcoItems;

    @Value("${application.xpath.icoName}")
    private String xpathIcoName;

    @Value("${application.xpath.icoDesc}")
    private String xpathIcoDesc;

    @Value("${application.xpath.itemAnchor}")
    private String xpathItemAnchor;

    @Value("${application.xpath.videoLinkElement}")
    private String xpathVideoLinkElement;

    @Value("${application.xpath.socialItems}")
    private String xpathSocialItems;

    @Value("${application.xpath.members}")
    private String xpathMembers;

    @JmsListener(destination = JmsConfig.HARVESTER_PROCESS_QUEUE)
    public void receiveIcoData(JmsMessage jmsMessage) {
        log.info("Received message on work.queue <" + jmsMessage + ">");

        HarvesterExecution harvesterExecution = harvesterExecutionService.findById(jmsMessage.getId());
        harvesterExecution.setStatus(HarvesterStatus.PROCESSING);
        harvesterExecutionService.save(harvesterExecution);

        List<IcoDetails> t = jmsMessage.getPageNumber() > 0 ? processPage(jmsMessage.getPageNumber(), harvesterExecution) : processAllPages(harvesterExecution);
        String jsonData = null;
        try {
            jsonData = new ObjectMapper().writeValueAsString(t);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        harvesterExecution.setPayload(jsonData);
        harvesterExecution.setStatus(HarvesterStatus.COMPLETED);
        harvesterExecutionService.save(harvesterExecution);
        log.info("Message has been processed");
    }

    private List<IcoDetails> processAllPages(HarvesterExecution currentJob) {
        Long startTime = System.currentTimeMillis();
        List<IcoDetails> allIcos = new ArrayList<>();

        boolean hasMorePages = true;
        int pageNum = 1;
        while (hasMorePages) {
            currentJob.setCurrentPage(pageNum);
            currentJob.setExecutionTime(System.currentTimeMillis() - startTime);
            harvesterExecutionService.save(currentJob);
            List<IcoDetails> pageIcos = processPage(pageNum, currentJob);
            if (pageIcos.isEmpty()) {
                hasMorePages = false;
            } else {
                allIcos.addAll(pageIcos);
            }
            pageNum++;
        }
        currentJob.setExecutionTime(System.currentTimeMillis() - startTime);
        harvesterExecutionService.save(currentJob);
        return allIcos;
    }

    private List<IcoDetails> processPage(int pageNum, HarvesterExecution currentJob) {
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        List<IcoDetails> pageIcos = new ArrayList<>();

        try {
            currentJob.setCurrentPage(pageNum);
            harvesterExecutionService.save(currentJob);

            String searchUrl = icoBenchBaseUrl + icoContext + pageQueryParam + pageNum;
            HtmlPage page = client.getPage(searchUrl);

            List<HtmlElement> items = (List<HtmlElement>) page.getByXPath(xpathIcoItems);
            if (!items.isEmpty()) {
                for (HtmlElement htmlItem : items) {
                    IcoDetails icoDetails = new IcoDetails();
                    HtmlElement icoName = htmlItem.getFirstByXPath(xpathIcoName);
                    HtmlElement icoDesc = htmlItem.getFirstByXPath(xpathIcoDesc);
                    HtmlAnchor itemAnchor = htmlItem.getFirstByXPath(xpathItemAnchor);

                    log.debug("Name [" + icoName.asText() + "]");

                    icoDetails.setIcoName(icoName.asText());
                    icoDetails.setIcoDescription(icoDesc.asText());

                    HtmlPage link = client.getPage(icoBenchBaseUrl + itemAnchor.getHrefAttribute());

                    DomAttr videoLinkElement = link.getFirstByXPath(xpathVideoLinkElement);
                    if (videoLinkElement != null) {
                        //Find all parameters between quotes
                        Matcher matcher = Pattern.compile("'[^']+'").matcher(videoLinkElement.getValue());
                        //Video link is first attribute
                        icoDetails.setVideoLink(matcher.find() ? matcher.group(0) : "No video available");
                    } else {
                        icoDetails.setVideoLink("No video available");
                    }

                    List<SocialMediaLinks> socialMediaLinks = new ArrayList<SocialMediaLinks>();
                    List<HtmlAnchor> socialItems = (List<HtmlAnchor>) link.getByXPath(xpathSocialItems);
                    if (!socialItems.isEmpty()) {
                        for (HtmlAnchor socialItem : socialItems) {
                            socialMediaLinks.add(new SocialMediaLinks(
                                    socialItem.asText(),
                                    socialItem.getHrefAttribute())
                            );
                        }
                    }

                    icoDetails.setSocialMediaLinks(socialMediaLinks);

                    List<TeamMembers> teamMembers = new ArrayList<TeamMembers>();
                    List<HtmlElement> members = (List<HtmlElement>) link.getByXPath(xpathMembers);
                    if (!members.isEmpty()) {
                        for (HtmlElement teamMember : members) {
                            HtmlElement name = teamMember.getFirstByXPath(".//h3");
                            HtmlElement position = teamMember.getFirstByXPath(".//h4");
                            String role = position == null ? "Advisor" : position.asText();

                            teamMembers.add(new TeamMembers(name.asText(), role));
                        }
                    }
                    icoDetails.setTeamMembers(teamMembers);
                    pageIcos.add(icoDetails);
                }
            }
            log.debug("Finished processing page " + pageNum);

        } catch (IOException e) {
            log.error(e.toString());
        }
        return pageIcos;
    }
}
