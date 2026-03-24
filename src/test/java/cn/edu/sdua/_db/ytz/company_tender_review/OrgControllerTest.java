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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrgControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    // 获取机构列表（平铺），默认 status=1（启用）
    void listFlatDefaultShouldReturnEnabledOrgs() throws Exception {
        mockMvc.perform(get("/api/orgs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1001))
                .andExpect(jsonPath("$.data[0].parentId").doesNotExist())
                .andExpect(jsonPath("$.data[0].typeName").value("审计机构"));
    }

    @Test
    // 获取机构列表（树形），包含 children
    void listTreeShouldReturnRootWithChildren() throws Exception {
        mockMvc.perform(get("/api/orgs").queryParam("tree", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1001))
                .andExpect(jsonPath("$.data[0].children.length()").value(1))
                .andExpect(jsonPath("$.data[0].children[0].id").value(2001))
                .andExpect(jsonPath("$.data[0].children[0].parentName").value("审计局"));
    }

    @Test
    // 创建机构，返回创建的机构节点
    void createShouldReturnCreatedNode() throws Exception {
        String body = """
                {
                  "name":"建设单位A",
                  "code":"ORG-3001",
                  "type":2,
                  "parentId":1001,
                  "address":"青岛"
                }
                """;
        MvcResult result = mockMvc.perform(post("/api/orgs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("建设单位A"))
                .andExpect(jsonPath("$.data.type").value(2))
                .andExpect(jsonPath("$.data.typeName").value("建设单位"))
                .andExpect(jsonPath("$.data.parentId").value(1001))
                .andExpect(jsonPath("$.data.parentName").value("审计局"))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        long id = json.path("data").path("id").asLong();
        assertThat(id).isGreaterThan(0);
    }

    @Test
    // 更新机构信息，支持更新 name/code/address/status
    void updateShouldChangeFields() throws Exception {
        String body = """
                {
                  "name":"审计局(改)",
                  "address":"济南市",
                  "status":1
                }
                """;
        mockMvc.perform(put("/api/orgs/1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1001))
                .andExpect(jsonPath("$.data.name").value("审计局(改)"))
                .andExpect(jsonPath("$.data.address").value("济南市"));
    }

    @Test
    // 创建机构参数校验失败
    void createValidationShouldFail() throws Exception {
        String body = """
                {
                  "name":"",
                  "type":9
                }
                """;
        mockMvc.perform(post("/api/orgs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4001));
    }
}

