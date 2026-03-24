package cn.edu.sdua._db.ytz.company_tender_review.service;

import cn.edu.sdua._db.ytz.company_tender_review.dto.response.LoginResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.UserDetailResponse;

public interface AuthService {
    LoginResponse login(String username, String password);

    LoginResponse refresh(String refreshToken);

    UserDetailResponse me(String bearerToken);

    void logout(String bearerToken);
}
