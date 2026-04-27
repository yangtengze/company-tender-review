package cn.edu.sdua._db.ytz.company_tender_review.service;

import cn.edu.sdua._db.ytz.company_tender_review.dto.request.BidAnnouncementCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ContractCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ExtractResultResponse;
import cn.edu.sdua._db.ytz.company_tender_review.repository.BidAnnouncementRepository;
import cn.edu.sdua._db.ytz.company_tender_review.repository.ContractRepository;
import cn.edu.sdua._db.ytz.company_tender_review.repository.DocumentExtractCacheRepository;
import cn.edu.sdua._db.ytz.company_tender_review.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class DocumentExtractService {
    private final DocumentRepository documentRepository;
    private final DocumentExtractCacheRepository cacheRepository;
    private final BidAnnouncementRepository bidAnnouncementRepository;
    private final ContractRepository contractRepository;
    
    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter D_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String PYTHON_AI_URL = "http://localhost:8000/api/ai/extract" ;
    private final RestTemplate restTemplate = new RestTemplate();

    public DocumentExtractService(DocumentRepository documentRepository,
                                  BidAnnouncementRepository bidAnnouncementRepository,
                                  ContractRepository contractRepository,
                                  DocumentExtractCacheRepository cacheRepository) {
        this.documentRepository = documentRepository;
        this.cacheRepository = cacheRepository;
        this.bidAnnouncementRepository = bidAnnouncementRepository;
        this.contractRepository = contractRepository;
    }

    public ExtractResultResponse extract(Long docId) {
        return cacheRepository.findByDocId(docId)
                .orElseGet(() -> {
                    Integer docType = documentRepository.findDocTypeById(docId);
                    if (docType == null) {
                        throw new IllegalArgumentException("document not found");
                    }
                    if (docType != 1 && docType != 5) {
                        throw new IllegalArgumentException("该文档类型暂不支持AI提取，请手动录入");
                    }
                    // 解析状态：标记为解析中（异步可扩展；当前实现同步写入缓存）
                    documentRepository.updateParseStatus(docId, 1);

                    try {
                        Map<String, Object> pyReq = new HashMap<>();
                        pyReq.put("doc_id", docId);
                        pyReq.put("doc_type", docType);
                        @SuppressWarnings("unchecked")
                        Map<String, Object> pyResp = restTemplate.postForObject(PYTHON_AI_URL, pyReq, Map.class);

                        if (pyResp == null || !Integer.valueOf(0).equals(pyResp.get("code"))) {
                            throw new RuntimeException("Python提取服务异常");
                        }

                        @SuppressWarnings("unchecked")
                        Map<String, Object> resultJson = (Map<String, Object>) pyResp.get("data");

                        DocumentRepository.DocumentMeta meta = documentRepository.findDocMetaById(docId);
                        Long projectId = meta.projectId();

                        if (docType == 1) {
                            BidAnnouncementCreateRequest req = buildBidRequest(docId, projectId, resultJson);
                            bidAnnouncementRepository.insert(req);
                        } else if (docType == 5) {
                            ContractCreateRequest req = buildContractRequest(docId, projectId, resultJson);
                            contractRepository.insert(req);
                        }

                        // 解析状态：标记为解析完成（异步可扩展；当前实现同步写入缓存）
                        documentRepository.updateParseStatus(docId, 2);

                        String extractType = (docType == 1) ? "bid_announcement" : "contract";
                        String modelName = (String) resultJson.getOrDefault("modelName", "unknown");

                        return cacheRepository.upsert(docId, extractType, modelName, resultJson);

                    } catch (Exception e) {
                        documentRepository.updateParseStatus(docId, 3);
                        throw new RuntimeException("AI提取失败: " + e.getMessage(), e);
                    }
                });
    }
    
    private BidAnnouncementCreateRequest buildBidRequest(Long docId, Long projectId, Map<String, Object> json) {
        BidAnnouncementCreateRequest req = new BidAnnouncementCreateRequest();
        req.setDocId(docId);
        req.setProjectId(projectId);
        
        req.setBidNo(getStr(json, "bidNo"));
        req.setBidType(getInt(json, "bidType"));

        String publishStr = getStr(json, "publishDate");
        req.setPublishDate(publishStr == null ? null : LocalDateTime.parse(publishStr, DT_FORMATTER));    
        
        String deadlineStr = getStr(json, "deadlineDate");
        req.setDeadlineDate(deadlineStr == null ? null : LocalDateTime.parse(deadlineStr, DT_FORMATTER));
        
        String openStr = getStr(json, "bidOpenDate");
        req.setBidOpenDate(openStr == null ? null : LocalDateTime.parse(openStr, DT_FORMATTER));
        
        req.setPlatformName(getStr(json, "platformName"));
        req.setPlatformUrl(getStr(json, "platformUrl"));
        req.setQualificationReq(getStr(json, "qualificationReq"));
        req.setPerformanceReq(getStr(json, "performanceReq"));
        req.setEstimatedPrice(getDecimal(json, "estimatedPrice"));

        return req;
    }
    
    private ContractCreateRequest buildContractRequest(Long docId, Long projectId, Map<String, Object> json) {
        ContractCreateRequest req = new ContractCreateRequest();
        req.setDocId(docId);
        req.setProjectId(projectId);
        
        req.setContractNo(getStr(json, "contractNo"));
        req.setContractAmount(getDecimal(json, "contractAmount"));

        String signStr = getStr(json, "signDate");
        req.setSignDate(signStr == null ? null : LocalDate.parse(signStr, D_FORMATTER));
        
        req.setPartyA(getStr(json, "partyA"));
        req.setPartyB(getStr(json, "partyB"));
        
        String startStr = getStr(json, "startDate");
        req.setStartDate(startStr == null ? null : LocalDate.parse(startStr, D_FORMATTER));
        
        String endStr = getStr(json, "endDate");
        req.setEndDate(endStr == null ? null : LocalDate.parse(endStr, D_FORMATTER));
        
        req.setWarrantyPeriod(getInt(json, "warrantyPeriod"));
        req.setPaymentTerms(getStr(json, "paymentTerms"));
        req.setPenaltyTerms(getStr(json, "penaltyTerms"));

        return req;
    }

    private String getStr(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val == null ? null : val.toString();
    }

    private Integer getInt(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null) return null;
        try { return Integer.valueOf(val.toString()); } 
        catch (Exception e) { return null; }
    }

    private BigDecimal getDecimal(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null) return null;
        try { return new BigDecimal(val.toString()); } 
        catch (Exception e) { return null; }
    }
}

