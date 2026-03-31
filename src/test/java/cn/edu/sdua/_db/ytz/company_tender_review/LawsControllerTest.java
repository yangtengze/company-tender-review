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
public class LawsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void ShouldReturnPage() throws Exception{
        mockMvc.perform(get("/api/laws")
                        .param("category", "2")
                        .param("status", "0")
                        .param("page", "1")
                        .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data[0].shortName").value("杨腾泽"));
    }

    @Test
    void InsertLawsText() throws Exception{
        String body = """
                {
                    "title": "测试",
                    "category": 1,
                    "fullText": "测试的fulltext",
                    "keywords":[]
                }
                """;
        mockMvc.perform(post("/api/laws")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data.categoryName").value("法律"));
    }
    @Test
    void ShouldReturnClauses() throws Exception {
        mockMvc.perform(get("/api/laws/1/clauses")
                        // .param("clauseNo", "第一条")
                        .param("keyword", "key1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data[0].content").value("测试1"))
                    .andExpect(jsonPath("$.data[1].content").value("测试2"));
    }
}
