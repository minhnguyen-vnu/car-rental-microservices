package com.iamservice.ui.restful.impl;

import com.iamservice.core.constant.response.GeneralResponse;
import com.iamservice.core.dto.request.AccountCreateRequest;
import com.iamservice.core.dto.request.AccountUpdateRequest;
import com.iamservice.core.dto.request.LoginRequest;
import com.iamservice.core.dto.response.LoginResponse;
import com.iamservice.core.entity.Account;
import com.iamservice.core.service.AuthService;
import com.iamservice.ui.restful.AuthController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {
    private final AuthService authService;

    @Override
    @PostMapping("/login")
    public GeneralResponse<LoginResponse> login(@RequestBody LoginRequest req) {
        return GeneralResponse.ok(authService.login(req));
    }

    @Override
    @GetMapping("/validate")
    public GeneralResponse<Account> validate(@RequestHeader("Authorization") String header) {
        return null;
    }

    @Override
    @PostMapping("/register")
    public GeneralResponse<Account> register(@RequestBody AccountCreateRequest req) {
        return GeneralResponse.ok(authService.register(req));
    }

    @Override
    @PutMapping("/update")
    public GeneralResponse<Account> update(@RequestBody AccountUpdateRequest req) {
        return GeneralResponse.ok(authService.update(req));
    }
}
