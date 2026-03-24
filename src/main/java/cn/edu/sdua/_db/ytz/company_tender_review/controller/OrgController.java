package cn.edu.sdua._db.ytz.company_tender_review.controller;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.OrgCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.OrgQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.OrgUpdateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.OrgNode;
import cn.edu.sdua._db.ytz.company_tender_review.repository.OrgRepository;
import cn.edu.sdua._db.ytz.company_tender_review.repository.model.OrgRow;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@Tag(name = "Org")
@RestController
@RequestMapping("/api/orgs")
public class OrgController {
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final OrgRepository orgRepository;

    public OrgController(OrgRepository orgRepository) {
        this.orgRepository = orgRepository;
    }

    @Operation(summary = "查询机构列表（平铺或树形）")
    @GetMapping
    public R<List<OrgNode>> list(@Valid OrgQueryRequest request) {
        List<OrgRow> rows = orgRepository.findAll(request.getType(), request.getStatus());
        boolean tree = Boolean.TRUE.equals(request.getTree());
        if (!tree) {
            return R.ok(toFlatNodes(rows));
        }
        return R.ok(toTreeNodes(rows));
    }

    @Operation(summary = "创建机构")
    @PostMapping
    public R<OrgNode> create(@Valid @RequestBody OrgCreateRequest request) {
        if (request.getParentId() != null && orgRepository.findById(request.getParentId()).isEmpty()) {
            throw new IllegalArgumentException("parent org not found");
        }
        long id = orgRepository.insert(request.getName(), request.getCode(), request.getType(), request.getParentId(), request.getAddress());
        OrgRow row = orgRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("org not found"));
        List<OrgRow> all = orgRepository.findAll(null, null);
        Map<Long, OrgRow> map = new HashMap<>();
        for (OrgRow r : all) {
            map.put(r.id(), r);
        }
        return R.ok(toNode(row, map));
    }

    @Operation(summary = "更新机构信息")
    @PutMapping("/{id}")
    public R<OrgNode> update(@PathVariable("id") Long id, @Valid @RequestBody OrgUpdateRequest request) {
        orgRepository.update(id, request.getName(), request.getCode(), request.getAddress(), request.getStatus());
        OrgRow row = orgRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("org not found"));
        List<OrgRow> all = orgRepository.findAll(null, null);
        Map<Long, OrgRow> map = new HashMap<>();
        for (OrgRow r : all) {
            map.put(r.id(), r);
        }
        return R.ok(toNode(row, map));
    }

    private List<OrgNode> toFlatNodes(List<OrgRow> rows) {
        Map<Long, OrgRow> map = new HashMap<>();
        for (OrgRow r : rows) {
            map.put(r.id(), r);
        }
        List<OrgNode> result = new ArrayList<>();
        for (OrgRow r : rows) {
            result.add(toNode(r, map));
        }
        return result;
    }

    private List<OrgNode> toTreeNodes(List<OrgRow> rows) {
        Map<Long, OrgNode> nodes = new HashMap<>();
        Map<Long, OrgRow> rowsById = new HashMap<>();
        for (OrgRow r : rows) {
            rowsById.put(r.id(), r);
        }
        for (OrgRow r : rows) {
            nodes.put(r.id(), toNode(r, rowsById));
        }
        List<OrgNode> roots = new ArrayList<>();
        for (OrgRow r : rows) {
            OrgNode node = nodes.get(r.id());
            Long parentId = r.parentId();
            if (parentId == null || !nodes.containsKey(parentId)) {
                roots.add(node);
                continue;
            }
            OrgNode parent = nodes.get(parentId);
            parent.ensureChildren();
            parent.getChildren().add(node);
        }
        return roots;
    }

    private OrgNode toNode(OrgRow row, Map<Long, OrgRow> rowsById) {
        OrgNode node = new OrgNode();
        node.setId(row.id());
        node.setName(row.name());
        node.setCode(row.code());
        node.setType(row.type());
        node.setTypeName(typeName(row.type()));
        node.setParentId(row.parentId());
        node.setParentName(parentName(row.parentId(), rowsById));
        node.setAddress(row.address());
        node.setStatus(row.status());
        node.setChildren(null);
        node.setCreatedAt(row.createdAt() == null ? null : row.createdAt().format(ISO_FORMATTER));
        return node;
    }

    private static String parentName(Long parentId, Map<Long, OrgRow> rowsById) {
        if (parentId == null) {
            return null;
        }
        OrgRow parent = rowsById.get(parentId);
        return parent == null ? null : parent.name();
    }

    private static String typeName(Integer type) {
        return switch (type == null ? 0 : type) {
            case 1 -> "审计机构";
            case 2 -> "建设单位";
            case 3 -> "施工单位";
            case 4 -> "监理单位";
            default -> "未知";
        };
    }
}
