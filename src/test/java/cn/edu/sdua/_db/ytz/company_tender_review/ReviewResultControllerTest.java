package cn.edu.sdua._db.ytz.company_tender_review;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.startsWith;

@SpringBootTest
@AutoConfigureMockMvc
class ReviewResultControllerTest {

    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listShouldReturnPage() throws Exception {
        mockMvc.perform(get("/api/review-results")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.data[0].reviewStatusName").value("待复核"));
    }

    @Test
    void reviewPatchShouldUpdateStatusAndNote() throws Exception {
        String token = accessToken(1L, "admin", 1);

        String body = """
                {
                  "reviewStatus": 2,
                  "reviewerNote": "已确认"
                }
                """;

        mockMvc.perform(patch("/api/review-results/1/review")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.reviewStatus").value(2))
                .andExpect(jsonPath("$.data.reviewStatusName").value("已确认"))
                .andExpect(jsonPath("$.data.reviewerName").value("系统管理员"))
                .andExpect(jsonPath("$.data.reviewerNote").value("已确认"));
    }

    @Test
    void exportShouldReturnPdfContentType() throws Exception {
        mockMvc.perform(get("/api/review-results/1/export")
                        .param("format", "pdf"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string(
                        "Content-Type", startsWith("application/pdf")))
                .andReturn();
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

