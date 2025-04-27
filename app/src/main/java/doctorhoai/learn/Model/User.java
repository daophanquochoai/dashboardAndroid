package doctorhoai.learn.Model;

import java.util.List;

public class User {
    private String email;
    private String id;
    private String name;
    private String cccd;
    private String sub;
    private Integer exp;
    private List<String> roles;

    public User(String email, String id, String name, String phone, String cccd, String sub, Integer exp, List<String> roles) {
        this.email = email;
        this.id = id;
        this.name = name;
        this.cccd = cccd;
        this.sub = sub;
        this.exp = exp;
        this.roles = roles;
    }
    public User(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
