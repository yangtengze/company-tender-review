package cn.edu.sdua._db.ytz.company_tender_review.service.impl;

import cn.edu.sdua._db.ytz.company_tender_review.dto.response.LoginResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.UserDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.repository.OperationLogRepository;
import cn.edu.sdua._db.ytz.company_tender_review.repository.UserRepository;
import cn.edu.sdua._db.ytz.company_tender_review.repository.model.UserAuthView;
import cn.edu.sdua._db.ytz.company_tender_review.service.AuthService;
import cn.edu.sdua._db.ytz.company_tender_review.service.security.JwtTokenService;
import cn.edu.sdua._db.ytz.company_tender_review.service.security.PasswordCodec;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AuthServiceImpl implements AuthService {
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final UserRepository userRepository;
    private final OperationLogRepository operationLogRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordCodec passwordCodec;

    public AuthServiceImpl(UserRepository userRepository,
                           OperationLogRepository operationLogRepository,
                           JwtTokenService jwtTokenService,
                           PasswordCodec passwordCodec) {
        this.userRepository = userRepository;
        this.operationLogRepository = operationLogRepository;
        this.jwtTokenService = jwtTokenService;
        this.passwordCodec = passwordCodec;
    }

    @Override
    public LoginResponse login(String username, String password) {
        UserAuthView user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("username or password incorrect"));
        if (!passwordCodec.matches(password, user.passwordHash())) {
            throw new IllegalArgumentException("username or password incorrect");
        }
        if (user.status() == null || user.status() != 1) {
            throw new IllegalArgumentException("user disabled");
        }

        LocalDateTime now = LocalDateTime.now();
        userRepository.updateLastLoginAt(user.id(), now);
        UserAuthView updatedUser = userRepository.findById(user.id()).orElse(user);
        return toLoginResponse(updatedUser);
    }

    @Override
    public LoginResponse refresh(String refreshToken) {
        Long userId = jwtTokenService.parseRefreshUserId(refreshToken);
        UserAuthView user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("refresh token invalid"));
        if (user.status() == null || user.status() != 1) {
            throw new IllegalArgumentException("user disabled");
        }
        return toLoginResponse(user);
    }

    @Override
    public UserDetailResponse me(String bearerToken) {
        Long userId = jwtTokenService.parseAccessUserId(bearerToken);
        UserAuthView user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("access token invalid"));
        if (user.status() == null || user.status() != 1) {
            throw new IllegalArgumentException("user disabled");
        }
        return toUserDetailResponse(user);
    }

    @Override
    public void logout(String bearerToken) {
        Long userId = jwtTokenService.parseAccessUserId(bearerToken);
        operationLogRepository.insertLogoutLog(userId);
    }

    private LoginResponse toLoginResponse(UserAuthView user) {
        LoginResponse response = new LoginResponse();
        response.setAccessToken(jwtTokenService.createAccessToken(user));
        response.setRefreshToken(jwtTokenService.createRefreshToken(user));
        response.setExpiresIn(jwtTokenService.getAccessExpireSeconds());
        response.setUserId(user.id());
        response.setUsername(user.username());
        response.setRealName(user.realName());
        response.setRole(user.role());
        response.setOrgId(user.orgId());
        response.setOrgName(user.orgName());
        return response;
    }

    private UserDetailResponse toUserDetailResponse(UserAuthView user) {
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

    private static String roleName(Integer role) {
        return switch (role == null ? 0 : role) {
            case 1 -> "超管";
            case 2 -> "审查员";
            case 3 -> "项目负责人";
            case 4 -> "只读";
            default -> "未知";
        };
    }

    private static String formatDateTime(LocalDateTime dt) {
        return dt == null ? null : dt.format(ISO_FORMATTER);
    }
}
