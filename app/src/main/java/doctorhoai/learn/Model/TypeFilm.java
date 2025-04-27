package doctorhoai.learn.Model;

import java.io.Serializable;

public class TypeFilm implements Serializable {
    private final long serialUID = 1L;
    private String id;
    private String name;
    private String active;

    public TypeFilm(String id, String name, String active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }
    public TypeFilm() {}

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

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
