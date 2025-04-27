package doctorhoai.learn.Model;

public class RevenueFilm {
     private String id;
     private String name;
     private String total_revenue;

     public RevenueFilm(String id, String name, String total_revenue) {
         this.id = id;
         this.name = name;
         this.total_revenue = total_revenue;
     }
     public RevenueFilm() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotal_revenue() {
        return total_revenue;
    }

    public void setTotal_revenue(String total_revenue) {
        this.total_revenue = total_revenue;
    }
}
