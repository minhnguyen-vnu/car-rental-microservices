package com.rentalservice.core.constant.exception;

import com.rentalservice.core.constant.enums.ErrorCode;
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
