package doctorhoai.learn.Model;

import org.jetbrains.annotations.NotNull;

public class EmployeeCreate {
    private String name;
    private String cccd;
    private String email;
    private String userName;
    private String password;
    private int roleId;
    private String status;

    public EmployeeCreate(String name, String CCCD, String email, String userName, String password, int roleId, String status) {
        this.name = name;
        this.cccd = CCCD;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.roleId = roleId;
        this.status = status;
    }
    public EmployeeCreate(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCCCD() {
        return cccd;
    }

    public void setCCCD(String CCCD) {
        this.cccd = CCCD;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
