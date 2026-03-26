package cn.edu.sdua._db.ytz.company_tender_review.service;

import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ExtractResultResponse;
import cn.edu.sdua._db.ytz.company_tender_review.repository.DocumentExtractCacheRepository;
import cn.edu.sdua._db.ytz.company_tender_review.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DocumentExtractService {
    private final DocumentRepository documentRepository;
    private final DocumentExtractCacheRepository cacheRepository;

    public DocumentExtractService(DocumentRepository documentRepository,
                                  DocumentExtractCacheRepository cacheRepository) {
        this.documentRepository = documentRepository;
        this.cacheRepository = cacheRepository;
    }

    public ExtractResultResponse extract(Long docId) {
        return cacheRepository.findByDocId(docId)
                .orElseGet(() -> {
                    Integer docType = documentRepository.findDocTypeById(docId);
                    if (docType == null) {
                        throw new IllegalArgumentException("document not found");
                    }
                    // 解析状态：标记为解析中（异步可扩展；当前实现同步写入缓存）
                    documentRepository.updateParseStatus(docId, 2);

                    String extractType = extractTypeByDocType(docType);
                    String modelName = "demo-llm";

                    Map<String, Object> resultJson = new HashMap<>();
                    resultJson.put("extractType", extractType);
                    resultJson.put("docType", docType);
                    resultJson.put("docId", docId);
                    resultJson.put("ok", true);

                    return cacheRepository.upsert(docId, extractType, modelName, resultJson);
                });
    }

    private static String extractTypeByDocType(int docType) {
        return switch (docType) {
            case 1 -> "bid_announcement";
            case 5 -> "contract";
            default -> "general";
        };
    }
}

