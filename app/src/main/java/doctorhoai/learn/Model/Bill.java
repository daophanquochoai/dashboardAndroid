package doctorhoai.learn.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.sql.Time;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Bill implements Serializable {
    private String id;
    private Float totalPrice;
    private String transactionCode;
    private String paymentMethodId;
    private String paymentMethod;
    private Active active;
    private String timestamp;
    private String status;
    private String qrCode;
    private String customerId;
    private Integer filmShowTimeId;
    private Time timeEnd;
    private Time timeStart;
    private String timeStampSee;
    private String roomId;
    private String nameRoom;
    private String nameBranch;
    private String address;
    private String filmId;
    private String nameFilm;
    private String userName;
    private String email;
    private String numberPhone;
    private List<BillChairDto> chairs;
    private List<BillDishDto> dishes;

    public Bill() {}

    public Bill(String id, Float totalPrice, String transactionCode, String paymentMethodId,
                String paymentMethod, Active active, String timestamp, String status,
                String qrCode, String customerId, Integer filmShowTimeId, Time timeEnd,
                Time timeStart, String timeStampSee, String roomId, String nameRoom,
                String nameBranch, String address, String filmId, String nameFilm,
                String userName, String email, String numberPhone,
                List<BillChairDto> chairs, List<BillDishDto> dishes) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.transactionCode = transactionCode;
        this.paymentMethodId = paymentMethodId;
        this.paymentMethod = paymentMethod;
        this.active = active;
        this.timestamp = timestamp;
        this.status = status;
        this.qrCode = qrCode;
        this.customerId = customerId;
        this.filmShowTimeId = filmShowTimeId;
        this.timeEnd = timeEnd;
        this.timeStart = timeStart;
        this.timeStampSee = timeStampSee;
        this.roomId = roomId;
        this.nameRoom = nameRoom;
        this.nameBranch = nameBranch;
        this.address = address;
        this.filmId = filmId;
        this.nameFilm = nameFilm;
        this.userName = userName;
        this.email = email;
        this.numberPhone = numberPhone;
        this.chairs = chairs;
        this.dishes = dishes;
    }

    public Bill(String id, String timestamp, Float totalPrice, String userName) {
        this.id = id;
        this.timestamp = timestamp;
        this.totalPrice = totalPrice;
        this.userName = userName;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Float getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Float totalPrice) { this.totalPrice = totalPrice; }

    public String getTransactionCode() { return transactionCode; }
    public void setTransactionCode(String transactionCode) { this.transactionCode = transactionCode; }

    public String getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Active getActive() { return active; }
    public void setActive(Active active) { this.active = active; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public Integer getFilmShowTimeId() { return filmShowTimeId; }
    public void setFilmShowTimeId(Integer filmShowTimeId) { this.filmShowTimeId = filmShowTimeId; }

    public Time getTimeEnd() { return timeEnd; }
    public void setTimeEnd(Time timeEnd) { this.timeEnd = timeEnd; }

    public Time getTimeStart() { return timeStart; }
    public void setTimeStart(Time timeStart) { this.timeStart = timeStart; }

    public String getTimeStampSee() { return timeStampSee; }
    public void setTimeStampSee(String timeStampSee) { this.timeStampSee = timeStampSee; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getNameRoom() { return nameRoom; }
    public void setNameRoom(String nameRoom) { this.nameRoom = nameRoom; }

    public String getNameBranch() { return nameBranch; }
    public void setNameBranch(String nameBranch) { this.nameBranch = nameBranch; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getFilmId() { return filmId; }
    public void setFilmId(String filmId) { this.filmId = filmId; }

    public String getNameFilm() { return nameFilm; }
    public void setNameFilm(String nameFilm) { this.nameFilm = nameFilm; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNumberPhone() { return numberPhone; }
    public void setNumberPhone(String numberPhone) { this.numberPhone = numberPhone; }

    public List<BillChairDto> getChairs() { return chairs; }
    public void setChairs(List<BillChairDto> chairs) { this.chairs = chairs; }

    public List<BillDishDto> getDishes() { return dishes; }
    public void setDishes(List<BillDishDto> dishes) { this.dishes = dishes; }
}