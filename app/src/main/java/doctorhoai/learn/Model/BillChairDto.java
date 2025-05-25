package doctorhoai.learn.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BillChairDto implements Serializable {
    private String id;
    private String chairCode;
    private Float price;
    private TicketDto ticket;
    private Active active;

    public BillChairDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getChairCode() { return chairCode; }
    public void setChairCode(String chairCode) { this.chairCode = chairCode; }

    public Float getPrice() { return price; }
    public void setPrice(Float price) { this.price = price; }

    public TicketDto getTicket() { return ticket; }
    public void setTicket(TicketDto ticket) { this.ticket = ticket; }

    public Active getActive() { return active; }
    public void setActive(Active active) { this.active = active; }
}