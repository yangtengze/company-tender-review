package cn.edu.sdua._db.ytz.company_tender_review;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@AutoConfigureMockMvc
class ChangeRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createShouldComputeChangeRatioAndReturnEmptyDocs() throws Exception {
        String body = """
                {
                  "projectId": 1,
                  "changeNo": "CR-TEST-001",
                  "changeType": 2,
                  "changeReason": 4,
                  "reasonDesc": "原因",
                  "changeDesc": "变更内容",
                  "changeAmount": 10000.00,
                  "applyOrgId": 1001
                }
                """;
        MvcResult result = mockMvc.perform(post("/api/change-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.projectId").value(1))
                .andExpect(jsonPath("$.data.status").value(1))
                .andExpect(jsonPath("$.data.statusName").value("待审查"))
                .andExpect(jsonPath("$.data.applyOrgName").value("审计局"))
                .andExpect(jsonPath("$.data.docs").isArray())
                .andExpect(jsonPath("$.data.docs.length()").value(0))
                .andExpect(jsonPath("$.data.changeRatio").value(1.1111))
                .andReturn();

        long id = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("id").asLong();
        assertThat(id).isGreaterThan(0);
    }

    @Test
    void bindDocShouldInsertAndPreventDuplicate() throws Exception {
        String body = """
                {
                  "projectId": 1,
                  "changeNo": "CR-TEST-002",
                  "changeType": 1,
                  "changeReason": 1,
                  "changeDesc": "内容",
                  "changeAmount": 1000.00,
                  "applyOrgId": 1001
                }
                """;
        MvcResult createResult = mockMvc.perform(post("/api/change-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();

        long id = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        mockMvc.perform(post("/api/change-requests/" + id + "/docs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"docId\":1,\"docRole\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from change_request_doc where change_request_id = ? and doc_id = ?",
                Integer.class, id, 1L
        );
        assertThat(count).isEqualTo(1);

        mockMvc.perform(post("/api/change-requests/" + id + "/docs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"docId\":1,\"docRole\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(409));
    }
}

