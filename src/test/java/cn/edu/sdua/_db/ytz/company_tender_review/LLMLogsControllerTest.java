package cn.edu.sdua._db.ytz.company_tender_review;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class LLMLogsControllerTest {
    // private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void ShouldReturnPage() throws Exception {
        // LocalDateTime now = LocalDateTime.now();
        // LocalDateTime plus1Second = now.plusSeconds(1);
        // String dateFrom = now.format(DT_FMT);
        // String dateTo = plus1Second.format(DT_FMT);
        mockMvc.perform(get("/api/llm-logs")
                        .param("taskId", "1")
                        .param("modelName", "test_model1")
                        .param("dateFrom", "2026-04-06T11:47:59")
                        .param("dateTo", "2026-04-06T11:48:00")
                        .param("page", "1")
                        .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[0].latencyMs").value(1000));
    }

    @Test
    void ShouldReturnList() throws Exception {
        mockMvc.perform(get("/api/llm-logs/summary")
                        .param("dateFrom", "2026-04-06T11:47:00")
                        .param("dateTo", "2026-04-07T11:48:00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data[0].modelName").value("test_model1"))
                    .andExpect(jsonPath("$.data[0].callCount").value(2))
                    .andExpect(jsonPath("$.data[0].failedCount").value(1))
                    .andExpect(jsonPath("$.data[1].modelName").value("test_model2"));
    }               
}
