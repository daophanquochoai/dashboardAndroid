package doctorhoai.learn.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketDto implements Serializable {
    private String id;
    private Active active;
    private String conditionUse;
    private String name;
    private Float price;
    private String typeTicket;
    private Integer slot;

    public TicketDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Active getActive() { return active; }
    public void setActive(Active active) { this.active = active; }

    public String getConditionUse() { return conditionUse; }
    public void setConditionUse(String conditionUse) { this.conditionUse = conditionUse; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Float getPrice() { return price; }
    public void setPrice(Float price) { this.price = price; }

    public String getTypeTicket() { return typeTicket; }
    public void setTypeTicket(String typeTicket) { this.typeTicket = typeTicket; }

    public Integer getSlot() { return slot; }
    public void setSlot(Integer slot) { this.slot = slot; }
}