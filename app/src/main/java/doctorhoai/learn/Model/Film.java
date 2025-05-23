package doctorhoai.learn.Model;

import java.io.Serializable;
import java.util.List;

public class Film implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private Integer age;
    private String image;
    private List<Sub> sub;
    private String nation;
    private String duration;
    private String description;
    private String content;
    private String trailer;
    private List<TypeFilm> typeFilms;
    private String status;

    public Film(String id, String name, Integer age, String image, List<Sub> sub, String nation, String duration, String description, String content, String trailer, List<TypeFilm> typeFilms, String status) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.image = image;
        this.sub = sub;
        this.nation = nation;
        this.duration = duration;
        this.description = description;
        this.content = content;
        this.trailer = trailer;
        this.typeFilms = typeFilms;
        this.status = status;
    }

    public Film() {}

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Sub> getSub() {
        return sub;
    }

    public void setSub(List<Sub> sub) {
        this.sub = sub;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public List<TypeFilm> getTypeFilms() {
        return typeFilms;
    }

    public void setTypeFilms(List<TypeFilm> typeFilms) {
        this.typeFilms = typeFilms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
