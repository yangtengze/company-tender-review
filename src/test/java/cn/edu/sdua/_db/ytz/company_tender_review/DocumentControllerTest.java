package cn.edu.sdua._db.ytz.company_tender_review;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void uploadListExtractShouldWorkAndReuseCache() throws Exception {
        // 1) login to get access token
        String loginBody = """
                {
                  "username":"admin",
                  "password":"123456"
                }
                """;
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();
        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String token = loginJson.path("data").path("accessToken").asText();

        // 2) upload
        MockMultipartFile mf = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "hello world".getBytes(StandardCharsets.UTF_8)
        );
        MvcResult uploadResult = mockMvc.perform(multipart("/api/documents/upload")
                        .file(mf)
                        .header("Authorization", "Bearer " + token)
                        .param("projectId", "1")
                        .param("docType", "1")
                        .param("issueDate", "2026-03-01")
                        .param("issuer", "审计局")
                        .param("version", "1.0")
                        .param("remark", "test remark")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.projectId").value(1))
                .andExpect(jsonPath("$.data.docType").value(1))
                .andExpect(jsonPath("$.data.docTypeName").value("招标公告"))
                .andExpect(jsonPath("$.data.docName").value("test.pdf"))
                .andExpect(jsonPath("$.data.parseStatus").value(0))
                .andReturn();

        JsonNode uploadJson = objectMapper.readTree(uploadResult.getResponse().getContentAsString());
        long docId = uploadJson.path("data").path("id").asLong();
        assertThat(docId).isGreaterThan(0);

        // 3) list by projectId and docType
        mockMvc.perform(get("/api/documents")
                        .param("projectId", "1")
                        .param("docType", "1")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].id").value(docId))
                .andExpect(jsonPath("$.data[0].docName").value("test.pdf"));

        // 4) extract first time -> cache created
        mockMvc.perform(get("/api/documents/" + docId + "/extract"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.docId").value(docId))
                .andExpect(jsonPath("$.data.extractType").value("bid_announcement"))
                .andExpect(jsonPath("$.data.resultJson.ok").value(true));

        Integer countAfterFirst = jdbcTemplate.queryForObject(
                "select count(*) from doc_extract_cache where doc_id = ?",
                Integer.class, docId
        );
        assertThat(countAfterFirst).isEqualTo(1);

        // 5) extract second time -> cache reused
        mockMvc.perform(get("/api/documents/" + docId + "/extract"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.docId").value(docId));

        Integer countAfterSecond = jdbcTemplate.queryForObject(
                "select count(*) from doc_extract_cache where doc_id = ?",
                Integer.class, docId
        );
        assertThat(countAfterSecond).isEqualTo(1);
    }
}

