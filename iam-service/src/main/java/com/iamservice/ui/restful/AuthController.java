package com.iamservice.ui.restful;


import com.iamservice.core.constant.response.GeneralResponse;
import com.iamservice.core.dto.request.AccountCreateRequest;
import com.iamservice.core.dto.request.AccountUpdateRequest;
import com.iamservice.core.dto.request.LoginRequest;
import com.iamservice.core.dto.response.LoginResponse;
import com.iamservice.core.entity.Account;
import org.springframework.web.bind.annotation.*;


public interface AuthController {

    public GeneralResponse<LoginResponse> login(LoginRequest req);

    public GeneralResponse<Account> validate(String header);

    public GeneralResponse<Account> register(AccountCreateRequest req);

    public GeneralResponse<Account> update(AccountUpdateRequest req);
}
