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
class BidAnnouncementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void createShouldSuccessAndComputePublicNoticeDays() throws Exception {
        String body = """
                {
                  "docId": 1,
                  "projectId": 1,
                  "bidNo": "BID-001",
                  "bidType": 1,
                  "publishDate": "2026-01-01T10:00:00",
                  "deadlineDate": "2026-01-11T10:00:00",
                  "bidOpenDate": "2026-01-12T10:00:00",
                  "platformName": "平台A",
                  "platformUrl": "https://example.com",
                  "estimatedPrice": 100000.00,
                  "qualificationReq": "资质要求",
                  "performanceReq": "业绩要求"
                }
                """;
        mockMvc.perform(post("/api/bid-announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.docId").value(1))
                .andExpect(jsonPath("$.data.projectId").value(1))
                .andExpect(jsonPath("$.data.bidTypeName").value("公开"))
                .andExpect(jsonPath("$.data.publicNoticeDays").value(10))
                .andExpect(jsonPath("$.data.updatedAt").isNotEmpty());
    }

    @Test
    void createShouldFailWhenDocTypeNotBidAnnouncement() throws Exception {
        String body = """
                {
                  "docId": 2,
                  "projectId": 1,
                  "bidType": 1
                }
                """;
        mockMvc.perform(post("/api/bid-announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4004));
    }
}

