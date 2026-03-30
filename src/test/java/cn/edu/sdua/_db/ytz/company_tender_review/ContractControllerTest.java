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

@SpringBootTest
@AutoConfigureMockMvc
class ContractControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void createShouldSuccessAndReturnUpdatedAt() throws Exception {
        String body = """
                {
                  "docId": 2,
                  "projectId": 1,
                  "contractNo": "CN-001",
                  "contractAmount": 500000.00,
                  "signDate": "2026-01-01",
                  "partyA": "甲方",
                  "partyB": "乙方",
                  "startDate": "2026-02-01",
                  "endDate": "2026-12-31",
                  "warrantyPeriod": 12,
                  "paymentTerms": "付款条款",
                  "penaltyTerms": "违约条款"
                }
                """;

        mockMvc.perform(post("/api/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.docId").value(2))
                .andExpect(jsonPath("$.data.projectId").value(1))
                .andExpect(jsonPath("$.data.contractNo").value("CN-001"))
                .andExpect(jsonPath("$.data.contractAmount").isNumber())
                .andExpect(jsonPath("$.data.contractAmount").value(500000.0))
                .andExpect(jsonPath("$.data.signDate").value("2026-01-01"))
                .andExpect(jsonPath("$.data.warrantyPeriod").value(12))
                .andExpect(jsonPath("$.data.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.data.updatedAt").isNotEmpty());
    }

    @Test
    void createShouldFailWhenDocTypeMismatch() throws Exception {
        String body = """
                {
                  "docId": 1,
                  "projectId": 1,
                  "contractNo": "CN-001",
                  "contractAmount": 500000.00
                }
                """;

        mockMvc.perform(post("/api/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4004));
    }
}

