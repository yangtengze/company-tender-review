package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class UserCreateRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 64, message = "用户名长度为4-64字符")
    @Pattern(regexp = "[a-zA-Z0-9_]+", message = "用户名只能包含字母、数字和下划线")
    private String username;


    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 32, message = "密码长度为8-32字符")
    private String password;


    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 64, message = "真实姓名长度不能超过64字符")
    private String realName;


    @Pattern(regexp = "1[3-9]\\d{9}", message = "手机号格式不正确")
    private String phone;


    @Email(message = "邮箱格式不正确")
    @Size(max = 128, message = "邮箱长度不能超过128字符")
    private String email;


    @NotNull(message = "组织ID不能为空")
    @Positive(message = "组织ID必须为正数")
    private Long orgId;


    @NotNull(message = "角色不能为空")
    @Min(value = 1, message = "角色最小为1")
    @Max(value = 4, message = "角色最大为4")
    private Integer role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }
}
