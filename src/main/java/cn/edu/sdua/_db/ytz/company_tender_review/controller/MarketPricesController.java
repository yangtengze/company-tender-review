package cn.edu.sdua._db.ytz.company_tender_review.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.MarketPriceCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.PriceCompareRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.PriceQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.BatchImportResult;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.MarketPriceItem;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.PriceTrendItem;
import cn.edu.sdua._db.ytz.company_tender_review.repository.MarketPricesRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Validated
@Tag(name = "MarketPrice")
@RestController
@RequestMapping("/api/market-prices")
public class MarketPricesController {
    private final MarketPricesRepository marketPricesRepository;

    public MarketPricesController(MarketPricesRepository marketPricesRepository) {
        this.marketPricesRepository = marketPricesRepository;
    }

    @Operation(summary = "查询市场价格")
    @GetMapping
    public R<List<MarketPriceItem>> list(@Valid PriceQueryRequest request) {
        try {
            Long total = marketPricesRepository.count(request);
            List<MarketPriceItem> data = marketPricesRepository.list(request);
            return R.okPage(data, total, request.getPage(), request.getSize());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    @Operation(summary = "批量导入市场价格，最多 1000 条")
    @PostMapping("/batch")
    public R<BatchImportResult> insert(@Valid @RequestBody List<MarketPriceCreateRequest> requests) {
        try {
            return R.ok(marketPricesRepository.insert(requests));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    @Operation(summary = "查询物料近 N 月价格时序（变更经济性图表数据源）")
    @GetMapping("/compare")
    public R<List<PriceTrendItem>> compare(@Valid PriceCompareRequest request) {
        try {
            return R.ok(marketPricesRepository.compare(request));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
