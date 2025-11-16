package com.iamservice.core.service.Impl;

import com.iamservice.core.constant.ErrorCode;
import com.iamservice.core.constant.exception.AppException;
import com.iamservice.core.context.LocalContextHolder;
import com.iamservice.core.dto.request.AccountCreateRequest;
import com.iamservice.core.dto.request.AccountUpdateRequest;
import com.iamservice.core.dto.request.LoginRequest;
import com.iamservice.core.dto.response.LoginResponse;
import com.iamservice.core.entity.Account;
import com.iamservice.core.service.AuthService;
import com.iamservice.infrastructure.repository.AccountRepository;
import com.iamservice.kernel.utils.DataUtils;
import com.iamservice.kernel.utils.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest req) {
        Account account = accountRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_ACCOUNT_NOT_FOUND));

        if (!BCrypt.checkpw(req.getPassword(), account.getPasswordHash())) {
            throw new AppException(ErrorCode.AUTH_INVALID_PASSWORD);
        }

        return new LoginResponse(jwtUtil.generateToken(account.getId().toString(), account.getRole()), jwtUtil.getExpirationMs());
    }

    @Override
    public Account register(AccountCreateRequest req) {
        if (DataUtils.isBlank(req.getUsername())) throw new AppException(ErrorCode.REQ_MISSING_FIELD, "username");
        if (DataUtils.isBlank(req.getEmail()))  throw new AppException(ErrorCode.REQ_MISSING_FIELD, "email");
        if (DataUtils.isBlank(req.getPhone()))  throw new AppException(ErrorCode.REQ_MISSING_FIELD, "phone");
        if (DataUtils.isBlank(req.getPassword())) throw new AppException(ErrorCode.REQ_MISSING_FIELD, "password");
        if (DataUtils.isBlank(req.getRealName())) throw new AppException(ErrorCode.REQ_MISSING_FIELD, "name");

        if (!DataUtils.isValidEmail(req.getEmail()))  throw new AppException(ErrorCode.REQ_INVALID_EMAIL);
        if (!DataUtils.isValidPhone(req.getPhone()))  throw new AppException(ErrorCode.REQ_INVALID_PHONE);
        if (!DataUtils.lengthBetween(req.getPassword(), 6, 50)) throw new AppException(ErrorCode.REQ_INVALID_PASSWORD);
        if (!DataUtils.lengthBetween(req.getUsername(), 6, 50)) throw new AppException(ErrorCode.REQ_INVALID_USERNAME);
        if (!DataUtils.lengthBetween(req.getRealName(), 6, 50)) throw new AppException(ErrorCode.REQ_INVALID_NAME);
        if (!DataUtils.isValidUsername(req.getUsername())) throw new AppException(ErrorCode.REQ_INVALID_USERNAME);

        accountRepository.findByUsername(req.getUsername()).ifPresent(a -> { throw new AppException(ErrorCode.AUTH_USERNAME_EXISTS); });
        accountRepository.findByEmail(req.getEmail()).ifPresent(a -> { throw new AppException(ErrorCode.AUTH_EMAIL_EXISTS); });
        accountRepository.findByPhone(req.getPhone()).ifPresent(a -> { throw new AppException(ErrorCode.AUTH_PHONE_EXISTS); });

        String hashed = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt());

        Account acc = Account.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .phone(req.getPhone())
                .passwordHash(hashed)
                .role(req.getRole() != null ? req.getRole() : "USER")
                .status(req.getStatus() != null ? req.getStatus() : "ACTIVE")
                .build();

        return accountRepository.save(acc);
    }

    @Override
    public Account update(AccountUpdateRequest req) {
        Account account = LocalContextHolder.get().getAccount();
        if (req.getEmail() != null) {
            if (!DataUtils.isValidEmail(req.getEmail()))  throw new AppException(ErrorCode.REQ_INVALID_EMAIL);
            account.setEmail(req.getEmail());
        }
        if (req.getPhone() != null) {
            if (!DataUtils.isValidPhone(req.getPhone()))  throw new AppException(ErrorCode.REQ_INVALID_PHONE);
            account.setPhone(req.getPhone());
        }
        if (req.getPassword() != null) {
            if (!DataUtils.lengthBetween(req.getPassword(), 6, 50)) throw new AppException(ErrorCode.REQ_INVALID_PASSWORD);
            String hashed = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt());
            account.setPasswordHash(hashed);
        }
        if (req.getRealName() != null) {
            if (!DataUtils.lengthBetween(req.getRealName(), 6, 50)) throw new AppException(ErrorCode.REQ_INVALID_NAME);
            account.setRealName(req.getRealName());
        }

        return accountRepository.save(account);
    }
}
