package doctorhoai.learn.Model;


import java.io.Serializable;

public class Dish implements Serializable {
    private String id;
    private String name;
    private Float price;
    private String active;
    private String image;
    private String typeDishId;

    public Dish() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Float getPrice() { return price; }
    public void setPrice(Float price) { this.price = price; }

    public String getActive() { return active; }
    public void setActive(String active) { this.active = active; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getTypeDish() { return typeDishId; }
    public void setTypeDish(String typeDishId) { this.typeDishId = typeDishId; }
}
