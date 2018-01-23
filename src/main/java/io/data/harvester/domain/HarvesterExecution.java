package io.data.harvester.domain;

import io.data.harvester.models.HarvesterStatus;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "harvester_execution")
public class HarvesterExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = Integer.MAX_VALUE)
    private String payload;
    private HarvesterStatus status;
    private Date lastUpdated;
    private int currentPage;
    private Long executionTime;

    public HarvesterExecution() {
        this.payload = "";
        this.status = HarvesterStatus.QUEUED;
        this.lastUpdated = new Date();
        this.currentPage = 0;
        this.executionTime = 0L;
    }

    public Long getId() {
        return id;
    }

    public HarvesterStatus getStatus() {
        return status;
    }

    public void setStatus(HarvesterStatus status) {
        this.status = status;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "HarvesterExecution{" +
                "id=" + id +
                ", executionTime=" + executionTime +
                ", status=" + status +
                ", payload='" + payload + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", currentPage=" + currentPage +
                '}';
    }
}
