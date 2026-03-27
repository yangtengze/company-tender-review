package cn.edu.sdua._db.ytz.company_tender_review;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReviewTaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createShouldSuccess() throws Exception {
        String body = """
                {
                  "projectId": 1,
                  "taskType": 1,
                  "taskName": "核心审查任务",
                  "docIds": [1,2],
                  "docRoles": {
                    "1": "公告",
                    "2": "合同"
                  },
                  "priority": 2,
                  "triggerMode": 1
                }
                """;
        MvcResult result = mockMvc.perform(post("/api/review-tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.taskName").value("核心审查任务"))
                .andExpect(jsonPath("$.data.taskType").value(1))
                .andExpect(jsonPath("$.data.status").value(1))
                .andExpect(jsonPath("$.data.docs.length()").value(2))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(json.path("data").path("taskNo").asText()).startsWith("RT-");
    }

    @Test
    void listShouldPageAndFilter() throws Exception {
        mockMvc.perform(get("/api/review-tasks")
                        .param("projectId", "1")
                        .param("taskType", "1")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.data[0].projectId").value(1));
    }

    @Test
    void resultShouldReturnFullResult() throws Exception {
        mockMvc.perform(get("/api/review-tasks/2/result"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.taskId").value(2))
                .andExpect(jsonPath("$.data.overallVerdict").value(2))
                .andExpect(jsonPath("$.data.verdictName").value("存在问题"))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.issues.length()").value(2));
    }
}

