package cn.edu.sdua._db.ytz.company_tender_review.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
// 通用分页参数对象 PageQuery：page/size/orderBy/order，含注解校验

public class PageQuery {
    @Min(1)
    private Integer page = 1;

    @Min(1)
    @Max(100)
    private Integer size = 20;

    private String orderBy = "createdAt";

    @Pattern(regexp = "ASC|DESC", message = "order must be ASC or DESC")
    private String order = "DESC";

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
