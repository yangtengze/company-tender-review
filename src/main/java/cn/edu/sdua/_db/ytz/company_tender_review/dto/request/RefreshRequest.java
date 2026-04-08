package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.NotBlank;

public class RefreshRequest {
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
