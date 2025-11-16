package com.paymentservice.core.exception;

import com.paymentservice.core.enums.ErrorCode;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
  private final ErrorCode error;

  public AppException(ErrorCode error) {
    super(error.getMessage());
    this.error = error;
  }

  public AppException(ErrorCode error, String detail) {
    super(detail);
    this.error = error;
  }
}
