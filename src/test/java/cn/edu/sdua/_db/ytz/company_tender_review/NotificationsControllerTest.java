package cn.edu.sdua._db.ytz.company_tender_review;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
public class NotificationsControllerTest {

    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void ShouldReturnPage() throws Exception {
        mockMvc.perform(get("/api/notifications")
                        .param("isRead", "0")
                        .param("type", "2")
                        .param("page", "1")
                        .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data[0].typeName").value("问题待处理"))
                    .andExpect(jsonPath("$.data[0].title").value("测试通知1"));
    }
    
    @Test
    void ShouldRead() throws Exception {
        mockMvc.perform(patch("/api/notifications/1/read"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0));
    }
    @Test
    void ShouldReadAll() throws Exception {
        String token = accessToken(1L, "admin", 1);
        mockMvc.perform(patch("/api/notifications/read-all")
                        .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0));
    }

    private static String accessToken(Long userId, String username, int role) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .claim("typ", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .signWith(key)
                .compact();
    }
}
