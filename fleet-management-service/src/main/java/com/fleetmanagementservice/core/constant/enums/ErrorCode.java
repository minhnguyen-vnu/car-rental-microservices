package com.fleetmanagementservice.core.constant.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // OK
    SUCCESS(200, "Success"),

    // 1xxx: Auth
    AUTH_NO_PERMISSION(1000, "No permission"),
    AUTH_ACCOUNT_NOT_FOUND(1001, "Account not found"),
    AUTH_INVALID_PASSWORD(1002, "Invalid password"),
    AUTH_TOKEN_INVALID(1003, "Invalid or expired token"),
    AUTH_USERNAME_EXISTS(1004, "Username already exists"),
    AUTH_EMAIL_EXISTS(1005, "Email already exists"),
    AUTH_PHONE_EXISTS(1006, "Phone already exists"),
    AUTH_ACCOUNT_INACTIVE(1007, "Account is not active"),

    // 2xxx: Validation
    REQ_MISSING_FIELD(2001, "Missing required field"),
    REQ_INVALID_EMAIL(2002, "Invalid email format"),
    REQ_INVALID_PHONE(2003, "Invalid phone format"),
    REQ_INVALID_PASSWORD(2004, "Invalid password"),
    REQ_INVALID_USERNAME(2005, "Invalid username"),
    REQ_INVALID_NAME(2006, "Invalid name declared"),
    REQ_BODY_NULL(2007, "Request body is null"),
    REQ_ID_REQUIRED(2008, "Id is required"),
    REQ_INVALID_TIME_RANGE(2009, "Invalid time range"),
    REQ_INVALID_DURATION(2010, "Invalid duration value"),
    REQ_INVALID_AMOUNT(2011, "Invalid amount value"),

    // 3xxx: Vehicle
    VEHICLE_NOT_FOUND(3001, "Vehicle not found"),
    VEHICLE_EXISTED(3002, "Vehicle existed"),
    VEHICLE_NOT_AVAILABLE(3003, "Vehicle is not available"),
    VEHICLE_NOT_AVAILABLE_THAT_BRANCH(3004, "Vehicle does not exist in request pick up branch"),
    VEHICLE_OVERLAP_WITH_EXISTED_RENTAL(3005, "Vehicle is not available within that time range"),
    VEHICLE_OVERLAP_WITH_VEHICLE_BLOCKS(3006, "Vehicle is not available within that time range"),

    // 4xxx: Branch
    BRANCH_NOT_FOUND(4001, "Branch not found"),
    BRANCH_EXISTED(4002, "Branch existed"),

    // 5xxx: Rental
    RENTAL_NOT_FOUND(5001, "Rental not found"),
    RENTAL_IDENTIFIER_REQUIRED(5002, "Id or transactionCode is required"),
    RENTAL_WRONG_STATUS_TRANSFORM(5003, "Status transformation not allowed"),

    // 6xxx: Payment
    PAYMENT_NOT_FOUND(6001, "Payment not found"),
    PAYMENT_PROVIDER_UNSUPPORTED(6002, "Unsupported payment provider"),
    PAYMENT_SIGNATURE_INVALID(6003, "Invalid provider signature"),
    PAYMENT_AMOUNT_MISMATCH(6004, "Amount mismatch"),
    PAYMENT_TMN_INVALID(6005, "Invalid terminal code"),
    PAYMENT_STATUS_FINALIZED(6006, "Payment already finalized"),
    PAYMENT_BUILD_URL_FAILED(6007, "Build checkout URL failed"),
    PAYMENT_EXTERNAL_CALL_FAILED(6008, "External call failed"),
    PAYMENT_METHOD_REQUIRED(6009, "Payment method is required"),
    PAYMENT_PROVIDER_REQUIRED(6010, "Payment provider is required"),
    PAYMENT_IDEMPOTENCY_CONFLICT(6011, "Idempotency conflict"),

    // 9xxx: System
    SYS_UNEXPECTED(9000, "Unexpected error"),
    KAFKA_UNEXPECTED(9001, "Kafka unexpected error");

    private final int code;
    private final String message;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.message = msg;
    }
}
