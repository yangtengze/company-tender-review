package cn.edu.sdua._db.ytz.company_tender_review.controller;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.LoginRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.RefreshRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.LoginResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.UserDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Tag(name = "Auth")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return R.ok(authService.login(request.getUsername(), request.getPassword()));
    }

    @Operation(summary = "刷新 Access Token")
    @PostMapping("/refresh")
    public R<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return R.ok(authService.refresh(request.getRefreshToken()));
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public R<UserDetailResponse> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return R.fail(401, "Invalid authorization header(Bearer )");
        }
        return R.ok(authService.me(authorization));
    }
    @Operation(summary = "登出，写操作日志，客户端丢弃 Token")
    @PostMapping("/logout")
    public R<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return R.fail(401, "Invalid authorization header(Bearer )");
        }
        authService.logout(authorization);
        return R.ok(null);
    }
}
