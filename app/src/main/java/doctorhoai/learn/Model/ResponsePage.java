package doctorhoai.learn.Model;

public class ResponsePage {
    private Integer page;
    private Integer totalPage;
    private Object data;

    public ResponsePage(Integer page, Integer totalPage, Object data) {
        this.page = page;
        this.totalPage = totalPage;
        this.data = data;
    }
    public ResponsePage(){}

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
