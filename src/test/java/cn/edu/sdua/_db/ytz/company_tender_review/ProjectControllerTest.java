package cn.edu.sdua._db.ytz.company_tender_review;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void listShouldReturnPage() throws Exception {
        mockMvc.perform(get("/api/projects").queryParam("page", "1").queryParam("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.data[0].projectNo").value("PRJ-2026-001"))
                .andExpect(jsonPath("$.data[0].statusName").value("招标中"));
    }

    @Test
    void createShouldSuccess() throws Exception {
        String body = """
                {
                  "projectNo":"PRJ-2026-002",
                  "projectName":"新项目",
                  "projectType":2,
                  "buildOrgId":1001,
                  "contractorId":2001,
                  "totalInvestment":1200000.00,
                  "contractAmount":1000000.00,
                  "location":"青岛",
                  "plannedStart":"2026-03-01",
                  "plannedEnd":"2026-12-01"
                }
                """;
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.projectNo").value("PRJ-2026-002"))
                .andExpect(jsonPath("$.data.status").value(1));
    }

    @Test
    void patchStatusShouldValidateStateMachine() throws Exception {
        mockMvc.perform(patch("/api/projects/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":3,\"actualStart\":\"2026-04-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value(3))
                .andExpect(jsonPath("$.data.actualStart").value("2026-04-01"));

        mockMvc.perform(patch("/api/projects/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4004));
    }

    @Test
    void statsShouldReturnAggregatedFields() throws Exception {
        mockMvc.perform(get("/api/projects/1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.tasksTotal").value(2))
                .andExpect(jsonPath("$.data.tasksDone").value(1))
                .andExpect(jsonPath("$.data.tasksRunning").value(1))
                .andExpect(jsonPath("$.data.issuesTotal").value(2))
                .andExpect(jsonPath("$.data.issuesPending").value(1))
                .andExpect(jsonPath("$.data.issuesResolved").value(1))
                .andExpect(jsonPath("$.data.changesTotal").value(2))
                .andExpect(jsonPath("$.data.changesPending").value(1))
                .andExpect(jsonPath("$.data.docsTotal").value(2))
                .andExpect(jsonPath("$.data.complianceRate").value(0.5));
    }
}
