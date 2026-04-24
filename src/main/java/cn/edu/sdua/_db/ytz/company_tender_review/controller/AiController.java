package cn.edu.sdua._db.ytz.company_tender_review.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import java.util.Map;

@Tag(name = "AI Chat")
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private static final String PYTHON_CHAT_URL = "http://localhost:8000/api/ai/prompt";
    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/prompt")
    public R<Map<String, Object>> chat(@RequestBody Map<String, Object> req) {
        @SuppressWarnings("unchecked")
        Map<String, Object> prompt = restTemplate.postForObject(PYTHON_CHAT_URL, req, Map.class);
        return R.ok(prompt);
    }
}
