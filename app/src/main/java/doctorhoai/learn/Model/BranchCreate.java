package doctorhoai.learn.Model;

public class BranchCreate {
    private String nameBranch;
    private String address;
    private String status;

    public BranchCreate(String nameBranch, String address, String status) {
        this.nameBranch = nameBranch;
        this.address = address;
        this.status = status;
    }

    public BranchCreate() {
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
