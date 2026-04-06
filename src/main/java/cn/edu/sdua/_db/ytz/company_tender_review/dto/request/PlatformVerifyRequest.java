package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import org.hibernate.validator.constraints.URL;
import org.springframework.util.StringUtils;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

public class PlatformVerifyRequest {
    @URL
    @Size(max = 512)
    private String url;
    @Size(max = 128)
    private String name;
    @AssertTrue(message = "url与name至少传一个")
    public boolean isValid() {
        return StringUtils.hasText(url) || StringUtils.hasText(name);
    }
    
    public String getName() {
        return name;
    }
    public String getUrl() {
        return url;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
