package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class OrgCreateRequest {
    @NotBlank(message = "机构名称不能为空")
    @Size(max = 128, message = "机构名称长度不能超过128字符")
    private String name;

    @Size(max = 64, message = "机构编码长度不能超过64字符")
    private String code;

    @NotNull(message = "机构类型不能为空")
    @Min(value = 1, message = "机构类型最小为1")
    @Max(value = 4, message = "机构类型最大为4")
    private Integer type;

    @Positive(message = "父机构ID必须为正数")
    private Long parentId;

    @Size(max = 256, message = "地址长度不能超过256字符")
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
