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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
public class ReviewIssuesControllerTest {
    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listShouldReturnPage() throws Exception {
        mockMvc.perform(get("/api/review-issues")
                .param("projectId", "1")
                .param("page", "1")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data[0].id").exists())
            .andExpect(jsonPath("$.data[0].title").value("问题1"))
            .andExpect(jsonPath("$.data[0].statusName").value("待整改"))
            .andExpect(jsonPath("$.data[0].projectName").value("示例项目A"))
            .andExpect(jsonPath("$.data[0].taskName").value("任务2"));
    }
    @Test
    void handlePatchShouldUpdateStatusAndNot() throws Exception {
        String token = accessToken(1L, "admin", 1);

        String body = """
                {
                    "status": 2,
                    "handleNote": "整改中"
                }
                """;
        mockMvc.perform(patch("/api/review-issues/1/handle")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value(2))
                .andExpect(jsonPath("$.data.statusName").value("整改中"))
                .andExpect(jsonPath("$.data.handledByName").value("系统管理员"));
    }
    @Test
    void ShouldReturnStatistic() throws Exception{
        mockMvc.perform(get("/api/review-issues/statistic")
                        .param("projectId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(2));
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
