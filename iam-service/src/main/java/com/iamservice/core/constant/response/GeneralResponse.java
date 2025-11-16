package com.iamservice.core.constant.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.iamservice.core.constant.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneralResponse<T> {
    private Status status;
    private T data;

    public static <T> GeneralResponse<T> ok(T data) {
        return GeneralResponse.<T>builder()
                .status(new Status(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage()))
                .data(data)
                .build();
    }

    public static <T> GeneralResponse<T> error(int code, String msg) {
        return GeneralResponse.<T>builder()
                .status(new Status(code, msg))
                .build();
    }

}
