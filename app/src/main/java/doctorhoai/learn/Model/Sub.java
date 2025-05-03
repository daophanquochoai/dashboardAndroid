package doctorhoai.learn.Model;

import java.io.Serializable;

public class Sub implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    public Sub(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public Sub(){}

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
}
