package doctorhoai.learn.Model;

public class Phim {
     private String id;
     private String name;
     private Integer age;
     private String image;
     private Sub sub;
     private String nation;
     private String duration;
     private String description;
     private String content;
     private String trailer;
     private TypeFilm typeFilms;
     private String status;

    public Phim(String id, String name, Integer age, String image, Sub sub, String nation, String duration, String description, String trailer, String content, TypeFilm typeFilms, String status) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.image = image;
        this.sub = sub;
        this.nation = nation;
        this.duration = duration;
        this.description = description;
        this.trailer = trailer;
        this.content = content;
        this.typeFilms = typeFilms;
        this.status = status;
    }
    public Phim(){}

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

    public Sub getSub() {
        return sub;
    }

    public void setSub(Sub sub) {
        this.sub = sub;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TypeFilm getTypeFilms() {
        return typeFilms;
    }

    public void setTypeFilms(TypeFilm typeFilms) {
        this.typeFilms = typeFilms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
