package com.iamservice.core.constant.exception.handler;

import com.iamservice.core.constant.ErrorCode;
import com.iamservice.core.constant.exception.AppException;
import com.iamservice.core.constant.response.GeneralResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public GeneralResponse<Void> handleApp(AppException ex) {
        ErrorCode e = ex.getError();
        return GeneralResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GeneralResponse<Void> handleOther(Exception ex) {
        return GeneralResponse.error(ErrorCode.SYS_UNEXPECTED.getCode(), ErrorCode.SYS_UNEXPECTED.getMessage());
    }
}
