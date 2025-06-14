package doctorhoai.learn.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Room implements Serializable {
    private String id;
    private String name;
    private List<List<Integer>> positionChair;
    private String branchId;
    private String status;
    private int slot;

    public Room() {
    }

    public Room(String id, String name, List<List<Integer>> positionChair, String branchId, String status, int slot) {
        this.id = id;
        this.name = name;
        this.positionChair = positionChair;
        this.branchId = branchId;
        this.status = status;
        this.slot = slot;
    }

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

    public List<List<Integer>> getPositionChair() {
        return positionChair;
    }

    public void setPositionChair(List<List<Integer>> positionChair) {
        this.positionChair = positionChair;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int tinhTongGhe() {
        int total = 0;
        if (positionChair != null) {
            for (List<Integer> row : positionChair) {
                if (row != null) {
                    for (Integer seatType : row) {
                        if (seatType == null) continue;
                        switch (seatType) {
                            case 1: // Ghế đơn
                                total += 1;
                                break;
                            case 2: // Ghế đôi
                                total += 2;
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        return total;
    }
}
