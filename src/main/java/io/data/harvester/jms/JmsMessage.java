package io.data.harvester.jms;

public class JmsMessage {

    private Long id;
    private int pageNumber;

    public JmsMessage() {
    }

    public JmsMessage(Long id) {
        this.id = id;
        this.pageNumber = 0;
    }

    public JmsMessage(Long id, int pageNumber) {
        this.id = id;
        this.pageNumber = pageNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public String toString() {
        return "JmsMessage{" +
                "id=" + id +
                ", pageNumber=" + pageNumber +
                '}';
    }
}
