package cn.edu.sdua._db.ytz.company_tender_review;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class CasesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void ShouldReturnPage() throws Exception {
        mockMvc.perform(get("/api/cases")
                        .param("caseType", "1")
                        .param("page", "1")
                        .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data[0].title").value("测试1"))
                    .andExpect(jsonPath("$.data[0].caseTypeName").value("招投标违规"));
    }

    @Test
    void ShouldReturnDetail() throws Exception {
        String body = """
                {
                    "title": "测试2",
                    "caseType": 2,
                    "projectType": 1,
                    "issueType": 1,
                    "description": "测试案例"
                }
                """;
        mockMvc.perform(post("/api/cases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data.caseTypeName").value("合同纠纷"))
                    .andExpect(jsonPath("$.data.title").value("测试2"));
    }
}
