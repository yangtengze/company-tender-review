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
public class MarketPricesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void ShouldReturnPage() throws Exception {
        mockMvc.perform(get("/api/market-prices")
                        .param("itemCode", "10")
                        .param("keyword", "测")
                        .param("category", "1")
                        .param("page", "1")
                        .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data[0].itemCode").value("1001"))
                    .andExpect(jsonPath("$.data[0].itemName").value("测试项目"))
                    .andExpect(jsonPath("$.data[0].categoryName").value("人工"));
    }

    @Test
    void ShouldReturnSummary() throws Exception {
        String body = """
                [
                    {
                        "itemCode" : "1001",
                        "itemName" : "测试项目1",
                        "category" : 2,
                        "price": 100.3,
                        "priceDate": "2026-04-02",
                        "region": "中国滨州"
                    },
                    {
                        "itemCode" : "1002",
                        "itemName" : "测试项目2",
                        "category" : 1,
                        "price" : 100.2,
                        "priceDate" : "2026-04-01"
                    }
                ]
                """;

        mockMvc.perform(post("/api/market-prices/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data.total").value(2))
                    .andExpect(jsonPath("$.data.updated").value(1))
                    .andExpect(jsonPath("$.data.failed").value(0));
    }
    @Test
    void ShouldReturnCompare() throws Exception {
        mockMvc.perform(get("/api/market-prices/compare")
                        .param("itemCode", "1001")
                        .param("region", "滨州")
                        .param("months", "3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data[0].price").value(100.1));
    }

}
