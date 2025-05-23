package doctorhoai.learn.Model;

public class BillParam {
    private String page;
    private String asc;
    private String limit;
    private String orderBy;
    private String q;
    private String active;

    public BillParam(String page, String asc, String limit, String orderBy, String q, String active) {
        this.page = page;
        this.asc = asc;
        this.limit = limit;
        this.orderBy = orderBy;
        this.q = q;
        this.active = active;
    }

    public String getPage() { return page; }
    public void setPage(String page) { this.page = page; }

    public String getAsc() { return asc; }
    public void setAsc(String asc) { this.asc = asc; }

    public String getLimit() { return limit; }
    public void setLimit(String limit) { this.limit = limit; }

    public String getOrderBy() { return orderBy; }
    public void setOrderBy(String orderBy) { this.orderBy = orderBy; }

    public String getQ() { return q; }
    public void setQ(String q) { this.q = q; }

    public String getActive() { return active; }
    public void setActive(String active) { this.active = active; }
}