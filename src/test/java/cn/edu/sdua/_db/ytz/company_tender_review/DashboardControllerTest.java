package cn.edu.sdua._db.ytz.company_tender_review;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void ShouldReturnOverview() throws Exception {
        mockMvc.perform(get("/api/dashboard/overview")
                        .param("orgId", "1001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data.activeProjects").value(1))
                    .andExpect(jsonPath("$.data.pendingTasks").value(1))
                    .andExpect(jsonPath("$.data.completedTasks").value(1))
                    .andExpect(jsonPath("$.data.highRiskIssues").value(0))
                    .andExpect(jsonPath("$.data.pendingChanges").value(1))
                    .andExpect(jsonPath("$.data.tokensThisMonth").value(0))
                    .andExpect(jsonPath("$.data.complianceRate").value(50.0))
                    .andExpect(jsonPath("$.data.avgReviewTime").value(0));
    }
    @Test
    void ShouldReturnStatsWithFilter() throws Exception {
        mockMvc.perform(get("/api/dashboard/dimension-stats")
                        .param("projectId", "1")
                        .param("taskType", "1")
                        .param("dateFrom", "2026-01-01T00:00:00")
                        .param("dateTo", "2026-12-31T23:59:59"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].compliant").value(1));
    }
    @Test
    void ShouldReturnTrendWithFilter() throws Exception {
        mockMvc.perform(get("/api/dashboard/issue-trend")
                        .param("projectId", "1")
                        .param("days", "7"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data[0].total").value(2));
    }
}
