package cn.edu.sdua._db.ytz.company_tender_review;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class PlatformsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void ShouldReturnList() throws Exception {
        mockMvc.perform(get("/api/platforms")
                        .param("level", "1")
                        .param("region", "滨州")
                        .param("isApproved", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data[0].levelName").value("国家"))
                    .andExpect(jsonPath("$.data[0].name").value("测试平台"));
    }

    @Test
    void ShouldReturnVerify() throws Exception {
        String body = """
                {
                    "url": "https://www.baidu.com/",
                    "name": "测试平台"
                }
                """;
        mockMvc.perform(patch("/api/platforms/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data.isApproved").value(true))
                    .andExpect(jsonPath("$.data.platformName").value("测试平台"))
                    .andExpect(jsonPath("$.data.matchScore").value(1));
    }
}
