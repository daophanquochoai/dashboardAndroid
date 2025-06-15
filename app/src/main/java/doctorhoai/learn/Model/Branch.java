package doctorhoai.learn.Model;

import java.io.Serializable;

public class Branch implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String nameBranch;
    private String address;
    private String status;

    public Branch(String id, String nameBranch, String address, String status) {
        this.id = id;
        this.nameBranch = nameBranch;
        this.address = address;
        this.status = status;
    }

    public Branch() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameBranch() {
        return nameBranch;
    }

    public void setNameBranch(String nameBranch) {
        this.nameBranch = nameBranch;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
