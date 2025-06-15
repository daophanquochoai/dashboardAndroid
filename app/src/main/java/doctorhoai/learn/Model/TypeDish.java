package doctorhoai.learn.Model;

import java.io.Serializable;
import java.util.List;

public class TypeDish implements Serializable {
    private String id;
    private String name;
    private String active;
    private List<Dish> dishes;

    public TypeDish() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getActive() { return active; }
    public void setActive(String active) { this.active = active; }

    public List<Dish> getDishes() { return dishes; }
    public void setDishes(List<Dish> dishes) { this.dishes = dishes; }
}