package cn.edu.sdua._db.ytz.company_tender_review.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.DashboardOverviewQuery;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.DimensionStatsQuery;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.IssueTrendQuery;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.DashboardOverviewResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.DimensionStatItem;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.TrendDataPoint;
import cn.edu.sdua._db.ytz.company_tender_review.repository.DashboardRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Validated
@Tag(name = "Dashboard")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardRepository dashboardRepository;

    public DashboardController(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    @Operation(summary = "首页大屏总览数据")
    @GetMapping("/overview")
    public R<DashboardOverviewResponse> overview(@Valid DashboardOverviewQuery request) {
        return R.ok(dashboardRepository.overview(request));
    }
    @Operation(summary = "按审查维度统计合规率（雷达图数据源）")
    @GetMapping("/dimension-stats")
    public R<List<DimensionStatItem>> stats(@Valid DimensionStatsQuery request) {
        return R.ok(dashboardRepository.stats(request));
    }
    @Operation(summary = "按日统计问题趋势（折线图数据源）")
    @GetMapping("/issue-trend")
    public R<List<TrendDataPoint>> trend(@Valid IssueTrendQuery request) {
        return R.ok(dashboardRepository.trend(request));
    }
}
