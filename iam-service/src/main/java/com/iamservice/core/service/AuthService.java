package com.iamservice.core.service;

import com.iamservice.core.dto.request.AccountCreateRequest;
import com.iamservice.core.dto.request.AccountUpdateRequest;
import com.iamservice.core.dto.request.LoginRequest;
import com.iamservice.core.dto.response.LoginResponse;
import com.iamservice.core.entity.Account;

public interface AuthService {
    public LoginResponse login(LoginRequest req);
    public Account register(AccountCreateRequest req);
    public Account update(AccountUpdateRequest req);
}
