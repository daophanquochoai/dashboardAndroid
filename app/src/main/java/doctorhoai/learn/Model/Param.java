package doctorhoai.learn.Model;

public class Param {
    private Integer page;
    private Integer limit;
    private String asc;
    private String orderBy;
    private String q;
    private String status;

    public Param(Integer page, String asc, Integer limit, String orderBy, String q, String status) {
        this.page = page;
        this.asc = asc;
        this.limit = limit;
        this.orderBy = orderBy;
        this.q = q;
        this.status = status;
    }

    public Param() {}

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getAsc() {
        return asc;
    }

    public void setAsc(String asc) {
        this.asc = asc;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
