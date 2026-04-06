package cn.edu.sdua._db.ytz.company_tender_review.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.NotificationQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.NotificationItem;
import cn.edu.sdua._db.ytz.company_tender_review.repository.NotificationsRepository;
import cn.edu.sdua._db.ytz.company_tender_review.service.security.JwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Validated
@Tag(name = "Notification")
@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {
    private final NotificationsRepository notificationsRepository;
    private final JwtTokenService jwtTokenService;

    public NotificationsController(NotificationsRepository notificationsRepository, JwtTokenService jwtTokenService) {
        this.notificationsRepository = notificationsRepository;
        this.jwtTokenService = jwtTokenService;
    }

    @Operation(summary = "获取当前用户通知列表")
    @GetMapping
    public R<List<NotificationItem>> list(@Valid NotificationQueryRequest request) {
        try {
            Long total = notificationsRepository.count(request);
            List<NotificationItem> data = notificationsRepository.list(request);
            return R.okPage(data, total, request.getPage(), request.getSize());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @Operation(summary = "将单条通知标记为已读")
    @PatchMapping("/{id}/read")
    public R<Void> read(@PathVariable("id") Long id) {
        try {
            notificationsRepository.read(id);
            return R.ok(null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    @Operation(summary = "将当前用户所有未读通知标记为已读")
    @PatchMapping("/read-all")
    public R<Void> readAll(@RequestHeader("Authorization") String authorization) {
        try {
            Long authId = jwtTokenService.parseAccessUserId(authorization);
            notificationsRepository.readAll(authId);
            return R.ok(null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
