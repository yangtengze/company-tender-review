package cn.edu.sdua._db.ytz.company_tender_review.controller;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ProjectCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ProjectQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ProjectStatusRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ProjectDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ProjectListItem;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ProjectStatsResponse;
import cn.edu.sdua._db.ytz.company_tender_review.repository.OrgRepository;
import cn.edu.sdua._db.ytz.company_tender_review.repository.ProjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@Tag(name = "Project")
@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectRepository projectRepository;
    private final OrgRepository orgRepository;

    public ProjectController(ProjectRepository projectRepository, OrgRepository orgRepository) {
        this.projectRepository = projectRepository;
        this.orgRepository = orgRepository;
    }

    @Operation(summary = "查询项目分页列表")
    @GetMapping
    public R<List<ProjectListItem>> list(@Valid ProjectQueryRequest request) {
        long total = projectRepository.count(request);
        List<ProjectListItem> data = projectRepository.list(request);
        return R.okPage(data, total, request.getPage(), request.getSize());
    }

    @Operation(summary = "创建新工程项目")
    @PostMapping
    public R<ProjectDetailResponse> create(@Valid @RequestBody ProjectCreateRequest request) {
        if (orgRepository.findById(request.getBuildOrgId()).isEmpty()) {
            throw new IllegalArgumentException("build org not found");
        }
        if (request.getContractorId() != null && orgRepository.findById(request.getContractorId()).isEmpty()) {
            throw new IllegalArgumentException("contractor org not found");
        }
        if (request.getSupervisorId() != null && orgRepository.findById(request.getSupervisorId()).isEmpty()) {
            throw new IllegalArgumentException("supervisor org not found");
        }
        long id = projectRepository.insert(request);
        return R.ok(projectRepository.findDetail(id));
    }

    @Operation(summary = "推进项目状态（状态机流转）")
    @PatchMapping("/{id}/status")
    public R<ProjectDetailResponse> updateStatus(@PathVariable("id") Long id,
                                                 @Valid @RequestBody ProjectStatusRequest request) {
        projectRepository.updateStatus(id, request.getStatus(), request.getActualStart(), request.getActualEnd());
        return R.ok(projectRepository.findDetail(id));
    }

    @Operation(summary = "项目汇总统计")
    @GetMapping("/{id}/stats")
    public R<ProjectStatsResponse> stats(@PathVariable("id") Long id) {
        return R.ok(projectRepository.stats(id));
    }
}
