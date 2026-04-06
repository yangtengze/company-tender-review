package cn.edu.sdua._db.ytz.company_tender_review.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.PlatformQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.PlatformVerifyRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.PlatformItem;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.PlatformVerifyResult;
import cn.edu.sdua._db.ytz.company_tender_review.repository.PlatformsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Validated
@Tag(name = "Platform")
@RestController
@RequestMapping("/api/platforms")
public class PlatformsController {
    private final PlatformsRepository platformsRepository;

    public PlatformsController(PlatformsRepository platformsRepository) {
        this.platformsRepository = platformsRepository;
    }

    @Operation(summary = "查询招标平台列表")
    @GetMapping
    public R<List<PlatformItem>> list(@Valid PlatformQueryRequest request) {
        try {
            return R.ok(platformsRepository.list(request));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    @Operation(summary = "根据 URL 或名称核验是否合规平台（供维度 11 实时调用）")
    @PatchMapping("/verify")
    public R<PlatformVerifyResult> verify(@Valid @RequestBody PlatformVerifyRequest request) {
        try {
            return R.ok(platformsRepository.verify(request));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
