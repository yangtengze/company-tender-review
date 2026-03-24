package cn.edu.sdua._db.ytz.company_tender_review.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.auth.jwt")
public record JwtProperties(
        String secret,
        long accessExpireSeconds,
        long refreshExpireSeconds
) {
}
