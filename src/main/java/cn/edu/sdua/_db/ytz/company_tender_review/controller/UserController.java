package cn.edu.sdua._db.ytz.company_tender_review.controller;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ChangePasswordRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.UserCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.UserQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.UserUpdateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.UserDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.UserListItem;
import cn.edu.sdua._db.ytz.company_tender_review.repository.OrgRepository;
import cn.edu.sdua._db.ytz.company_tender_review.repository.UserRepository;
import cn.edu.sdua._db.ytz.company_tender_review.repository.model.UserAuthView;
import cn.edu.sdua._db.ytz.company_tender_review.repository.model.UserListRow;
import cn.edu.sdua._db.ytz.company_tender_review.service.security.JwtTokenService;
import cn.edu.sdua._db.ytz.company_tender_review.service.security.PasswordCodec;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Validated
@Tag(name = "User")
@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final OrgRepository orgRepository;
    private final PasswordCodec passwordCodec;

    public UserController(JwtTokenService jwtTokenService,
                          UserRepository userRepository,
                          OrgRepository orgRepository,
                          PasswordCodec passwordCodec) {
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
        this.orgRepository = orgRepository;
        this.passwordCodec = passwordCodec;
    }

    @Operation(summary = "查询用户分页列表")
    @GetMapping
    public R<List<UserListItem>> list(@RequestHeader("Authorization") String authorization,
                                      @Valid UserQueryRequest request) {
        requireAdmin(authorization);

        long total = userRepository.countUsers(request.getOrgId(), request.getRole(), request.getStatus(), request.getKeyword());
        List<UserListRow> rows = userRepository.listUsers(
                request.getOrgId(), request.getRole(), request.getStatus(), request.getKeyword(),
                request.getPage(), request.getSize()
        );

        List<UserListItem> data = new ArrayList<>();
        for (UserListRow row : rows) {
            data.add(toListItem(row));
        }
        return R.okPage(data, total, request.getPage(), request.getSize());
    }

    @Operation(summary = "创建新用户，密码 BCrypt 加密后入库")
    @PostMapping
    public R<UserDetailResponse> create(@RequestHeader("Authorization") String authorization,
                                        @Valid @RequestBody UserCreateRequest request) {
        requireAdmin(authorization);
        if (orgRepository.findById(request.getOrgId()).isEmpty()) {
            throw new IllegalArgumentException("org not found");
        }
        String passwordHash = passwordCodec.bcrypt(request.getPassword());
        long id = userRepository.insertUser(
                request.getUsername(),
                passwordHash,
                request.getRealName(),
                request.getPhone(),
                request.getEmail(),
                request.getOrgId(),
                request.getRole()
        );
        UserAuthView user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("user not found"));
        return R.ok(toDetail(user));
    }

    @Operation(summary = "更新用户信息，超管可改 role/status")
    @PutMapping("/{id}")
    public R<UserDetailResponse> update(@RequestHeader("Authorization") String authorization,
                                        @PathVariable("id") Long id,
                                        @Valid @RequestBody UserUpdateRequest request) {
        requireAdmin(authorization);
        userRepository.updateUser(id, request.getRealName(), request.getPhone(), request.getEmail(),
                request.getRole(), request.getStatus(), request.getAvatarUrl());
        UserAuthView user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("user not found"));
        return R.ok(toDetail(user));
    }

    @Operation(summary = "修改密码；超管可强制重置他人")
    @PatchMapping("/{id}/pwd")
    public R<Void> changePassword(@RequestHeader("Authorization") String authorization,
                                  @PathVariable("id") Long id,
                                  @Valid @RequestBody ChangePasswordRequest request) {
        UserAuthView current = requireAdmin(authorization);
        if (request.getNewPassword() == null || request.getConfirmPassword() == null
                || !request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("confirm password mismatch");
        }

        boolean isSelf = current.id().equals(id);
        if (isSelf) {
            String old = request.getOldPassword();
            if (old == null || old.isBlank()) {
                throw new IllegalArgumentException("old password required");
            }
            String stored = userRepository.findPasswordHashById(id)
                    .orElseThrow(() -> new IllegalArgumentException("user not found"));
            if (!passwordCodec.matches(old, stored)) {
                throw new IllegalArgumentException("old password incorrect");
            }
        }

        userRepository.updatePasswordHash(id, passwordCodec.bcrypt(request.getNewPassword()));
        userRepository.updateLastLoginAt(id, LocalDateTime.now());
        return R.ok(null);
    }

    private UserAuthView requireAdmin(String authorization) {
        Long userId = jwtTokenService.parseAccessUserId(authorization);
        UserAuthView current = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("access token invalid"));
        if (current.role() == null || current.role() != 1) {
            throw new IllegalArgumentException("forbidden");
        }
        return current;
    }

    private static UserListItem toListItem(UserListRow row) {
        UserListItem item = new UserListItem();
        item.setId(row.id());
        item.setUsername(row.username());
        item.setRealName(row.realName());
        item.setPhone(row.phone());
        item.setEmail(row.email());
        item.setRole(row.role());
        item.setRoleName(roleName(row.role()));
        item.setStatus(row.status());
        item.setOrgId(row.orgId());
        item.setOrgName(row.orgName());
        item.setLastLoginAt(formatDateTime(row.lastLoginAt()));
        item.setCreatedAt(formatDateTime(row.createdAt()));
        return item;
    }

    private static UserDetailResponse toDetail(UserAuthView user) {
        UserDetailResponse response = new UserDetailResponse();
        response.setId(user.id());
        response.setUsername(user.username());
        response.setRealName(user.realName());
        response.setPhone(user.phone());
        response.setEmail(user.email());
        response.setRole(user.role());
        response.setRoleName(roleName(user.role()));
        response.setStatus(user.status());
        response.setOrgId(user.orgId());
        response.setOrgName(user.orgName());
        response.setOrgType(user.orgType());
        response.setAvatarUrl(user.avatarUrl());
        response.setLastLoginAt(formatDateTime(user.lastLoginAt()));
        response.setCreatedAt(formatDateTime(user.createdAt()));
        return response;
    }

    private static String formatDateTime(java.time.LocalDateTime dt) {
        return dt == null ? null : dt.format(ISO_FORMATTER);
    }

    private static String roleName(Integer role) {
        return switch (role == null ? 0 : role) {
            case 1 -> "超管";
            case 2 -> "审查员";
            case 3 -> "项目负责人";
            case 4 -> "只读";
            default -> "未知";
        };
    }
}

