package io.data.harvester.models;

import java.util.List;

public class IcoDetails {
    private String icoName;
    private String icoDescription;
    private String icoImageUrl;
    private String videoLink;
    private List<SocialMediaLinks> socialMediaLinks;
    private List<TeamMembers> teamMembers;

    public String getIcoName() {
        return icoName;
    }

    public void setIcoName(String icoName) {
        this.icoName = icoName;
    }

    public String getIcoDescription() {
        return icoDescription;
    }

    public void setIcoDescription(String icoDescription) {
        this.icoDescription = icoDescription;
    }

    public String getIcoImageUrl() {
        return icoImageUrl;
    }

    public void setIcoImageUrl(String icoImageUrl) {
        this.icoImageUrl = icoImageUrl;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public List<SocialMediaLinks> getSocialMediaLinks() {
        return socialMediaLinks;
    }

    public void setSocialMediaLinks(List<SocialMediaLinks> socialMediaLinks) {
        this.socialMediaLinks = socialMediaLinks;
    }

    public List<TeamMembers> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<TeamMembers> teamMembers) {
        this.teamMembers = teamMembers;
    }
}