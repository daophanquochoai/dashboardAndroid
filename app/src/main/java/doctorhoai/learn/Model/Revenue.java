package doctorhoai.learn.Model;

public class Revenue {
    private Integer month;
    private String totalPrice;

    public Revenue(Integer month, String totalPrice ){
        this.month = month;
        this.totalPrice = totalPrice;
    }
    public Revenue(){}

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
